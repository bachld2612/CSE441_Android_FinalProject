package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.entity.DeCuong;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.enums.DeCuongState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DeCuongMapper;
import com.bachld.project.backend.repository.DeCuongRepository;
import com.bachld.project.backend.repository.DeTaiRepository;
import com.bachld.project.backend.service.DeCuongService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DeCuongServiceImpl implements DeCuongService {

    DeCuongRepository deCuongRepository;
    DeTaiRepository deTaiRepository;
    DeCuongMapper mapper;

    @Override
    public boolean existsByDeTaiId(Long deTaiId) {
        return deCuongRepository.existsByDeTaiId(deTaiId);
    }

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongResponse submitDeCuong(DeCuongRequest request) {
        if (request == null || request.getDeTaiId() == null) {
            throw new ApplicationException(ErrorCode.DE_TAI_ID_EMPTY);      // 1203
        }
        if (!StringUtils.hasText(request.getFileUrl())) {
            throw new ApplicationException(ErrorCode.FILE_URL_EMPTY);       // 1202
        }

        DeTai deTai = deTaiRepository.findById(request.getDeTaiId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND)); // thêm vào ErrorCode

        // SV chỉ được nộp đề tài của mình
        String email = currentUsername();
        boolean isOwner = deTai.getSinhVienThucHien() != null
                && deTai.getSinhVienThucHien().getTaiKhoan() != null
                && email.equalsIgnoreCase(deTai.getSinhVienThucHien().getTaiKhoan().getEmail());
        if (!isOwner) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        // Tạo mới / cập nhật -> set PENDING
        DeCuong dc = deCuongRepository.findByDeTaiId(deTai.getId())
                .map(existing -> {
                    mapper.update(existing, request);
                    existing.setTrangThai(DeCuongState.PENDING);
                    return existing;
                })
                .orElseGet(() -> {
                    DeCuong created = mapper.toEntity(request);
                    created.setDeTai(deTai);
                    created.setTrangThai(DeCuongState.PENDING);
                    return created;
                });

        return mapper.toResponse(deCuongRepository.save(dc));
    }

    @Override
    public DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve) {
        DeCuong dc = deCuongRepository.findById(deCuongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_CUONG_NOT_FOUND)); // thêm vào ErrorCode

        // GV chỉ duyệt đề cương mình hướng dẫn
        String email = currentUsername();
        boolean isSupervisor = dc.getDeTai() != null
                && dc.getDeTai().getGvhd() != null
                && dc.getDeTai().getGvhd().getTaiKhoan() != null
                && email.equalsIgnoreCase(dc.getDeTai().getGvhd().getTaiKhoan().getEmail());
        if (!isSupervisor) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        if (dc.getTrangThai() == DeCuongState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED); // cần khai báo thêm, ví dụ 1207
        }
        if (dc.getTrangThai() == DeCuongState.CANCELED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_REJECTED); // ví dụ 1208
        }


        dc.setTrangThai(approve ? DeCuongState.ACCEPTED : DeCuongState.CANCELED);
        return mapper.toResponse(deCuongRepository.save(dc));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN','SCOPE_ADMIN','SCOPE_TRUONG_BO_MON')")
    @Override
    public Page<DeCuongResponse> getAllDeCuong(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean isGV = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_GIANG_VIEN"));

        if (isGV) {
            return deCuongRepository
                    .findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCase(email, pageable)
                    .map(mapper::toResponse);
        }

        return deCuongRepository.findAll(pageable).map(mapper::toResponse);
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

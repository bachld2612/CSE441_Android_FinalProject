package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.entity.DeCuong;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.enums.DeCuongState;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DeCuongMapper;
import com.bachld.project.backend.repository.DeCuongRepository;
import com.bachld.project.backend.repository.DeTaiRepository;
import com.bachld.project.backend.repository.GiangVienRepository;
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

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DeCuongServiceImpl implements DeCuongService {

    DeCuongRepository deCuongRepository;
    DeTaiRepository deTaiRepository;
    GiangVienRepository giangVienRepository;
    DeCuongMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongResponse submitDeCuong(DeCuongRequest request) {
        DeTai deTai = deTaiRepository.findById(request.getDeTaiId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        if (deTai.getTrangThai() != DeTaiState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_TAI_NOT_ACCEPTED);
        }
        // Nếu bạn dùng enum khác, hãy đổi điều kiện cho phù hợp.

        // (B) Chỉ SV chủ đề tài được nộp
        String email = currentUsername();
        boolean isOwner = deTai.getSinhVienThucHien() != null
                && deTai.getSinhVienThucHien().getTaiKhoan() != null
                && email.equalsIgnoreCase(deTai.getSinhVienThucHien().getTaiKhoan().getEmail());
        if (!isOwner) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        // (C) Tạo mới / cập nhật
        DeCuong dc = deCuongRepository.findByDeTai_Id(deTai.getId())
                .map(existing -> {
                    if (existing.getTrangThai() == DeCuongState.ACCEPTED) {
                        // Đã duyệt rồi thì không cho thay đổi
                        throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
                    }
                    // Cho nộp lại: cập nhật URL, về PENDING, tăng số lần nộp, xóa lý do cũ
                    mapper.update(existing, request);
                    existing.setTrangThai(DeCuongState.PENDING);
                    existing.setSoLanNop(existing.getSoLanNop() + 1);
                    existing.setNhanXet(null);
                    return existing;
                })
                .orElseGet(() -> {
                    DeCuong created = mapper.toEntity(request);
                    created.setDeTai(deTai);
                    created.setTrangThai(DeCuongState.PENDING);
                    created.setSoLanNop(1);
                    created.setNhanXet(null);
                    return created;
                });

        return mapper.toResponse(deCuongRepository.save(dc));
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRUONG_BO_MON')")
    @Override
    public DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve, String reason) {
        // 1) Lấy GV hiện tại theo email
        String email = currentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_EmailIgnoreCase(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        // 2) Lấy đề cương
        DeCuong dc = deCuongRepository.findById(deCuongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_CUONG_NOT_FOUND));

        // 3) Chỉ GV hướng dẫn của đề tài đó mới được duyệt
        if (dc.getDeTai() == null || dc.getDeTai().getGvhd() == null
                || !dc.getDeTai().getGvhd().getId().equals(gv.getId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        // 4) Chỉ cho review khi đang PENDING
        if (dc.getTrangThai() == DeCuongState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
        }
        if (dc.getTrangThai() == DeCuongState.CANCELED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_REJECTED);
        }
        if (dc.getTrangThai() != DeCuongState.PENDING) {
            throw new ApplicationException(ErrorCode.OUTLINE_NOT_PENDING);
        }

        // 5) Áp trạng thái & lý do
        if (approve) {
            dc.setTrangThai(DeCuongState.ACCEPTED);
            // Ghi nhận xét khi duyệt (optional)
            if (reason != null && !reason.isBlank()) {
                dc.setNhanXet(reason.trim());
            }
        } else {
            // Reject -> bắt buộc có lý do
            if (reason == null || reason.isBlank()) {
                // dùng mã 1219 như đã thống nhất
                throw new ApplicationException(ErrorCode.DE_CUONG_REASON_REQUIRED);
            }
            dc.setTrangThai(DeCuongState.CANCELED);
            dc.setNhanXet(reason.trim());
        }

        return mapper.toResponse(deCuongRepository.save(dc));
    }


    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN','SCOPE_TRUONG_BO_MON')")
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

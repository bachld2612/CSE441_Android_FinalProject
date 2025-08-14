package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.donhoandoan.DonHoanDoAnRequest;
import com.bachld.project.backend.dto.response.donhoandoan.DonHoanDoAnResponse;
import com.bachld.project.backend.entity.DonHoanDoAn;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.enums.HoanState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DonHoanDoAnMapper;
import com.bachld.project.backend.repository.DonHoanDoAnRepository;
import com.bachld.project.backend.repository.SinhVienRepository;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.DonHoanDoAnService;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class DonHoanDoAnServiceImpl implements DonHoanDoAnService {

    DonHoanDoAnRepository donHoanDoAnRepository;
    SinhVienRepository sinhVienRepository;
    DonHoanDoAnMapper donHoanDoAnMapper;
    CloudinaryService cloudinaryService; // đã có trong dự án của bạn

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DonHoanDoAnResponse createPostponeRequest(DonHoanDoAnRequest request) {
        String email = currentEmail();

        SinhVien sv = sinhVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));

        if (sv.getDeTai() != null) {
            throw new ApplicationException(ErrorCode.POSTPONE_NOT_ALLOWED_WHEN_HAS_DE_TAI);
        }

        if (donHoanDoAnRepository.existsBySinhVien_IdAndTrangThai(sv.getId(), HoanState.PENDING)) {
            throw new ApplicationException(ErrorCode.DON_HOAN_ALREADY_PENDING);
        }

        DonHoanDoAn don = new DonHoanDoAn();
        don.setSinhVien(sv);
        don.setTrangThai(HoanState.PENDING);
        don.setLyDo(request.getLyDo());
        don.setRequestedAt(LocalDateTime.now());

        if (request.getMinhChungFile() != null && !request.getMinhChungFile().isEmpty()) {
            try {
                String url = cloudinaryService.upload(request.getMinhChungFile());
                don.setMinhChungUrl(url);
            } catch (Exception e) {
                throw new ApplicationException(ErrorCode.DON_HOAN_FILE_UPLOAD_FAILED);
            }
        }

        return donHoanDoAnMapper.toResponse(donHoanDoAnRepository.save(don));
    }

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public Page<DonHoanDoAnResponse> getMyPostponeRequests(Pageable pageable) {
        String email = currentEmail();

        SinhVien sv = sinhVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));

        return donHoanDoAnRepository.findBySinhVien_Id(sv.getId(), pageable)
                .map(donHoanDoAnMapper::toResponse);
    }

    private String currentEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return auth.getName(); // hệ thống của bạn đang set email làm username
    }
}

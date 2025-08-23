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
import org.springframework.web.multipart.MultipartFile;

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

    private static final long MAX_PDF_BYTES = 10 * 1024 * 1024; // 10MB

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DonHoanDoAnResponse createPostponeRequest(DonHoanDoAnRequest request) {
        SinhVien sv = getCurrentSinhVienOrThrow();

        assertEligibleNoDeTai(sv);
        assertNoPendingRequest(sv.getId());

        DonHoanDoAn don = buildDraftDonHoanDoAn(sv, request.getLyDo());

        // ---- dùng helper: chỉ khi có file minh chứng ----
        if (hasFile(request.getMinhChungFile())) {
            String pdfUrl = uploadMinhChungAsPdfOrThrow(request.getMinhChungFile());
            don.setMinhChungUrl(pdfUrl);               // URL raw -> thường .pdf
            // Nếu có field contentType: don.setMinhChungContentType("application/pdf");
        }

        return donHoanDoAnMapper.toResponse(donHoanDoAnRepository.save(don));
    }

    // ================== Helpers (PRIVATE) ==================

    private boolean hasFile(MultipartFile f) {
        return f != null && !f.isEmpty();
    }

    /** Validate & upload file PDF ở dạng RAW để URL ra .pdf */
    private String uploadMinhChungAsPdfOrThrow(MultipartFile file) {
        validatePdf(file);
        try {
            return cloudinaryService.uploadRawFile(file); // đã set resource_type=raw trong service của bạn
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.DON_HOAN_FILE_UPLOAD_FAILED);
        }
    }

    /** Chỉ chấp nhận PDF, kiểm tra MIME + magic bytes + size */
    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApplicationException(ErrorCode.FILE_EMPTY);
        }
        String ct = file.getContentType();
        boolean mimeOk = "application/pdf".equalsIgnoreCase(ct);
        boolean magicOk = looksLikePdf(file); // %PDF

        if (!(mimeOk || magicOk)) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        if (file.getSize() > MAX_PDF_BYTES) {
            throw new ApplicationException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    private boolean looksLikePdf(MultipartFile file) {
        try (var is = file.getInputStream()) {
            byte[] header = is.readNBytes(4);
            return header.length == 4
                    && header[0] == 0x25 // '%'
                    && header[1] == 0x50 // 'P'
                    && header[2] == 0x44 // 'D'
                    && header[3] == 0x46; // 'F'
        } catch (Exception e) {
            return false;
        }
    }

    private SinhVien getCurrentSinhVienOrThrow() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        String email = auth.getName();
        return sinhVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));
    }

    private void assertEligibleNoDeTai(SinhVien sv) {
        if (sv.getDeTai() != null) {
            throw new ApplicationException(ErrorCode.POSTPONE_NOT_ALLOWED_WHEN_HAS_DE_TAI);
        }
    }

    private void assertNoPendingRequest(Long sinhVienId) {
        if (donHoanDoAnRepository.existsBySinhVien_IdAndTrangThai(sinhVienId, HoanState.PENDING)) {
            throw new ApplicationException(ErrorCode.DON_HOAN_ALREADY_PENDING);
        }
    }

    private DonHoanDoAn buildDraftDonHoanDoAn(SinhVien sv, String lyDo) {
        DonHoanDoAn don = new DonHoanDoAn();
        don.setSinhVien(sv);
        don.setTrangThai(HoanState.PENDING);
        don.setLyDo(lyDo);
        don.setRequestedAt(LocalDateTime.now());
        return don;
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

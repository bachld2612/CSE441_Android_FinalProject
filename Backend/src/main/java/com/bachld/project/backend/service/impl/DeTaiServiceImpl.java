package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.detai.DeTaiGiangVienHuongDanRequest;
import com.bachld.project.backend.dto.request.detai.DeTaiRequest;
import com.bachld.project.backend.dto.request.detai.DeTaiApprovalRequest;
import com.bachld.project.backend.dto.response.detai.DeTaiGiangVienHuongDanResponse;
import com.bachld.project.backend.dto.response.detai.DeTaiResponse;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.*;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DeTaiMapper;
import com.bachld.project.backend.repository.*;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.DeTaiService;
import com.bachld.project.backend.service.util.TimeGatekeeper;
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

import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DeTaiServiceImpl implements DeTaiService {

    DeTaiRepository deTaiRepository;
    SinhVienRepository sinhVienRepository;
    GiangVienRepository giangVienRepository;
    CloudinaryService cloudinaryService;
    DeTaiMapper deTaiMapper;
    TimeGatekeeper timeGatekeeper;

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRO_LY_KHOA', 'SCOPE_TRUONG_BO_MON')")
    public DeTaiResponse approveByGiangVien(Long deTaiId, String nhanXet) {
        DeTaiApprovalRequest req = new DeTaiApprovalRequest(true, nhanXet);
        return approveDeTai(deTaiId, req);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRO_LY_KHOA', 'SCOPE_TRUONG_BO_MON')")
    public DeTaiResponse rejectByGiangVien(Long deTaiId, String nhanXet) {
        DeTaiApprovalRequest req = new DeTaiApprovalRequest(false, nhanXet);
        return approveDeTai(deTaiId, req);
    }

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeTaiResponse registerDeTai(DeTaiRequest request) {
        // get sinh viên
        String accountEmail = getCurrentUsername();
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Email(accountEmail)
                        .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));
        ThoiGianThucHien thoiGianDangKy = timeGatekeeper.validateThoiGianDangKy();
        DotBaoVe dotBaoVe = thoiGianDangKy.getDotBaoVe();
        DeTai deTai = sv.getDeTai();

        if (deTai == null) {
            deTai = deTaiMapper.toDeTai(request);
            deTai.setSinhVienThucHien(sv);
        } else {
            if(deTai.getTrangThai() == DeTaiState.ACCEPTED){
                throw  new ApplicationException(ErrorCode.DE_TAI_ALREADY_ACCEPTED);
            }
            deTaiMapper.update(request, deTai);
        }

        deTai.setTrangThai(DeTaiState.PENDING);

        if (request.getFileTongQuan() != null && !request.getFileTongQuan().isEmpty()) {
            String url = upload(request.getFileTongQuan());
            deTai.setTongQuanDeTaiUrl(url);
        }

        deTai.setDotBaoVe(dotBaoVe);
        DeTai saved = deTaiRepository.save(deTai);
        return deTaiMapper.toDeTaiResponse(saved);
    }

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeTaiResponse getMyDeTai() {
        String accountEmail = getCurrentUsername();
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Email(accountEmail)
                .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));

        DeTai deTai = deTaiRepository.findBySinhVienThucHien_Id(sv.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        return deTaiMapper.toDeTaiResponse(deTai);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public DeTaiGiangVienHuongDanResponse addGiangVienHuongDan(DeTaiGiangVienHuongDanRequest request) {
        SinhVien sv = sinhVienRepository.findByMaSV(request.getMaSV())
                .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));
        GiangVien gv = giangVienRepository.findByMaGV(request.getMaGV()).
                orElseThrow(() -> new ApplicationException(ErrorCode.GIANG_VIEN_NOT_FOUND));
        Optional<DeTai> deTai = deTaiRepository.findBySinhVienThucHien_Id(sv.getId());
        if(deTai.isPresent()) {
            throw new ApplicationException(ErrorCode.SINH_VIEN_ALREADY_REGISTERED_DE_TAI);
        }
        DeTai newDeTai = DeTai.builder()
                .sinhVienThucHien(sv)
                .gvhd(gv)
                .build();
        deTaiRepository.save(newDeTai);
        return DeTaiGiangVienHuongDanResponse.builder()
                .success(true)
                .build();
    }

    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIEN')")
    @Override
    public Page<DeTaiResponse> getDeTaiByLecturerAndStatus(DeTaiState trangThai, Pageable pageable) {
        String email = getCurrentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_Email((email))
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_GVHD_OF_DE_TAI));
        Long gvhdId = gv.getId();

        var page = deTaiRepository.findByGvhd_IdAndTrangThai(gvhdId, trangThai, pageable);
        return page.map(deTaiMapper::toDeTaiResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIEN')")
    @Override
    public DeTaiResponse approveDeTai(Long deTaiId, DeTaiApprovalRequest request) {
        // 1) load đề tài
        DeTai detai = deTaiRepository.findById(deTaiId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        String email = getCurrentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_Email((email))
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_GVHD_OF_DE_TAI));
        Long gvhdId = gv.getId();

        // 2) xác thực đúng GVHD
        if (detai.getGvhd() == null || !gvhdId.equals(detai.getGvhd().getId())) {
            throw new ApplicationException(ErrorCode.NOT_GVHD_OF_DE_TAI);
        }

        // 3) chỉ cho duyệt khi đang PENDING
        if (detai.getTrangThai() != DeTaiState.PENDING) {
            throw new ApplicationException(ErrorCode.DE_TAI_NOT_IN_PENDING_STATUS);
        }

        // 4) chuyển trạng thái + lưu nhận xét
        if (Boolean.TRUE.equals(request.getApproved())) {
            detai.setTrangThai(DeTaiState.ACCEPTED);
        } else if (Boolean.FALSE.equals(request.getApproved())) {
            detai.setTrangThai(DeTaiState.CANCELED);
        } else {
            throw new ApplicationException(ErrorCode.TRANG_THAI_INVALID);
        }
        detai.setNhanXet(request.getNhanXet());

        return deTaiMapper.toDeTaiResponse(deTaiRepository.save(detai));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try { return auth.getName(); }
        catch (Exception e) { throw new ApplicationException(ErrorCode.UNAUTHENTICATED); }
    }

    private String upload(org.springframework.web.multipart.MultipartFile file) {
        try { return cloudinaryService.upload(file); }
        catch (Exception e) { throw new ApplicationException(ErrorCode.UPLOAD_FILE_FAILED); }
    }
}

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
import com.bachld.project.backend.enums.CongViec;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DeTaiMapper;
import com.bachld.project.backend.repository.*;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.DeTaiService;
import com.bachld.project.backend.util.TimeGatekeeper;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
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
    private static final ZoneId ZONE_BKK = ZoneId.of("Asia/Bangkok");
    private final ThoiGianThucHienRepository thoiGianThucHienRepository;

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
        String accountEmail = getCurrentUsername();
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Email(accountEmail)
                        .orElseThrow(() -> new ApplicationException(ErrorCode.SINH_VIEN_NOT_FOUND));

        if (sv.getGpa() < 2.0){
            throw new IllegalArgumentException("Sinh viên dưới 2.0 GPA không được tham gia đồ án");
        }

        if(request.getTenDeTai().trim().isEmpty()){
            throw new IllegalArgumentException("Tên đề tài không được bỏ trống");
        }

        if(request.getGvhdId() == null){
            throw new IllegalArgumentException("Giảng viên hướng dẫn không được bỏ trống");
        }

        LocalDate today = LocalDate.now(ZONE_BKK);
        Optional<ThoiGianThucHien> thoiGianDangKyOp = thoiGianThucHienRepository
                .findTopByCongViecAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqualOrderByThoiGianBatDauDesc(CongViec.DANG_KY_DE_TAI, today, today);

        if (thoiGianDangKyOp.isEmpty()){
            throw new IllegalArgumentException("Thời gian đăng ký chưa mở, vui lòng liên hệ với phòng đạo tạo để biết thêm chi tiết");
        }

        DotBaoVe dotBaoVe = thoiGianDangKyOp.get().getDotBaoVe();
        DeTai deTai = sv.getDeTai();
        Optional<DeTai> existingDeTai = deTaiRepository.findByTenDeTaiIgnoreCase(request.getTenDeTai());
        if (deTai == null) {
            deTai = deTaiMapper.toDeTai(request);
            deTai.setSinhVienThucHien(sv);
            if(existingDeTai.isPresent()){
                throw new IllegalArgumentException("Đề tài đã tồn tại, vui lòng thực hiện đề tài khác ");
            }
        } else {
            if(deTai.getTrangThai() == DeTaiState.ACCEPTED){
                    throw new IllegalArgumentException("Đề tài đã được duyệt, không thể đổi");
            }

            if(existingDeTai.isPresent() && !Objects.equals(deTai.getId(), existingDeTai.get().getId())){
                throw new IllegalArgumentException("Đề tài đã tồn tại, vui lòng thực hiện đề tài khác ");
            }
            deTaiMapper.update(request, deTai);
        }




        deTai.setTrangThai(DeTaiState.PENDING);

        if (request.getFileTongQuan() != null && !request.getFileTongQuan().isEmpty()) {
            if(request.getFileTongQuan().getSize() > 10 * 1024 * 1024){
                throw new IllegalArgumentException("File tổng quan không được quá 10MB");
            }
            String url = upload(request.getFileTongQuan());

            String fileExtension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
            if (!fileExtension.equals("doc") && !fileExtension.equals("pdf")) {
                throw new IllegalArgumentException("File tổng quan chỉ chấp nhận định dạng .doc hoặc .pdf");
            }
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
        DeTai detai = deTaiRepository.findById(deTaiId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        String email = getCurrentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_Email((email))
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_GVHD_OF_DE_TAI));
        Long gvhdId = gv.getId();

        if (detai.getGvhd() == null || !gvhdId.equals(detai.getGvhd().getId())) {
            throw new IllegalArgumentException("Chỉ giảng viên hướng dẫn mới được duyệt hoặc từ chối đề tài");
        }

        if (detai.getTrangThai() != DeTaiState.PENDING) {
            throw new IllegalArgumentException("Chỉ đề tài trạng thái đang chờ mới được duyệt hoặc từ chối");
        }

        if (Boolean.TRUE.equals(request.getApproved())) {
            detai.setTrangThai(DeTaiState.ACCEPTED);
        } else if (Boolean.FALSE.equals(request.getApproved())) {
            detai.setTrangThai(DeTaiState.CANCELED);
            if(request.getNhanXet().trim().isEmpty()){
                throw new IllegalArgumentException("Lý do chọn đề tài không được bỏ trống khi từ chối");
            }
            detai.setNhanXet(request.getNhanXet());
        } else {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
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

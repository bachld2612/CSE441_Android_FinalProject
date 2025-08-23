package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.GiangVienUpdateRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.*;
import com.bachld.project.backend.entity.*;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.GiangVienMapper;
import com.bachld.project.backend.mapper.SinhVienMapper;
import com.bachld.project.backend.repository.BoMonRepository;
import com.bachld.project.backend.repository.GiangVienRepository;
import com.bachld.project.backend.repository.SinhVienRepository;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.GiangVienService;
import com.bachld.project.backend.util.TimeGatekeeper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class GiangVienServiceImpl implements GiangVienService {

    GiangVienRepository giangVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoMonRepository boMonRepository;
    private final GiangVienMapper giangVienMapper;
    SinhVienRepository sinhVienRepository;
    SinhVienMapper sinhVienMapper;
    private final TimeGatekeeper timeGatekeeper;

    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRO_LY_KHOA', 'SCOPE_TRUONG_BO_MON')")
    @Override
    public Page<SinhVienSupervisedResponse> getMySinhVienSupervised(Pageable pageable) {
        String email = currentEmail();

        Long gvhdId = giangVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_A_GVHD))
                .getId();

        DotBaoVe dotBaoVe = timeGatekeeper.getCurrentDotBaoVe();
        Page<SinhVien> page = sinhVienRepository.findByDeTai_Gvhd_IdAndDeTai_DotBaoVe(gvhdId, dotBaoVe, pageable);
        return page.map(sinhVienMapper::toSinhVienSupervisedResponse);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRO_LY_KHOA', 'SCOPE_TRUONG_BO_MON')")
    @Override
    public Page<DeTaiSinhVienApprovalResponse> getDeTaiSinhVienApproval(DeTaiState status, Pageable pageable) {
        String email = currentEmail();

        Long gvhdId = giangVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_A_GVHD))
                .getId();

        DotBaoVe dotBaoVe = timeGatekeeper.getCurrentDotBaoVe();

        Page<SinhVien> page = (status == null)
                ? sinhVienRepository.findByDeTai_Gvhd_IdAndDeTai_DotBaoVe(gvhdId,dotBaoVe, pageable)
                : sinhVienRepository.findByDeTai_Gvhd_IdAndDeTai_TrangThaiAndDeTai_DotBaoVe(gvhdId, status, dotBaoVe, pageable);

        return page.map(sinhVienMapper::toDeTaiSinhVienApprovalResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Set<GiangVienInfoResponse> getGiangVienByBoMonAndSoLuongDeTai(Long boMonId) {
        BoMon boMon = boMonRepository.findById(boMonId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));
        Set<GiangVien> giangVienSet = giangVienRepository.findAvailableGiangVienByBoMon(boMonId);
        Set<GiangVienInfoResponse> responses = giangVienSet.stream()
                .map(giangVienMapper::toGiangVienInfoResponse)
                .collect(Collectors.toSet());
        responses.forEach(response -> {
            int soLuongDeTai = giangVienRepository.countDeTaiByGiangVienAndSinhVienActive(response.getMaGV());
            response.setSoLuongDeTai(soLuongDeTai);
        });
        return responses;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRO_LY_KHOA', 'SCOPE_TRUONG_BO_MON')")
    @Override
    public List<StudentSupervisedResponse> getMySinhVienSupervisedAll(String q) {
        String email = currentEmail();

        Long gvhdId = giangVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_A_GVHD))
                .getId();

        DotBaoVe dotBaoVe = timeGatekeeper.getCurrentDotBaoVe();

        final List<SinhVien> list = (q == null || q.isBlank())
                ? sinhVienRepository.findByDeTai_Gvhd_IdAndDeTai_DotBaoVeOrderByHoTenAsc(gvhdId, dotBaoVe)
                : sinhVienRepository.searchMySupervisedAll(gvhdId, dotBaoVe, q.trim());

        // map sang DTO response
        return list.stream()
                .map(sinhVienMapper::toStudentSupervisedResponse)
                .toList();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
    @Override
    public GiangVienCreationResponse createGiangVien(GiangVienCreationRequest giangVienCreationRequest) {

        if(giangVienRepository.existsByMaGV(giangVienCreationRequest.getMaGV())) {
            throw new ApplicationException(ErrorCode.MA_GV_EXISTED);
        }
        if(taiKhoanRepository.existsByEmail((giangVienCreationRequest.getEmail()))) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan currentUser = taiKhoanRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .email(giangVienCreationRequest.getEmail())
                .matKhau(passwordEncoder.encode(giangVienCreationRequest.getMatKhau()))
                .vaiTro(Role.GIANG_VIEN)
                .build();

        BoMon boMon = boMonRepository.findById(giangVienCreationRequest.getBoMonId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));

        GiangVien giangVien = GiangVien.builder()
                .hocVi(giangVienCreationRequest.getHocVi())
                .hocHam(giangVienCreationRequest.getHocHam())
                .maGV(giangVienCreationRequest.getMaGV())
                .hoTen(giangVienCreationRequest.getHoTen())
                .boMon(boMon)
                .soDienThoai(giangVienCreationRequest.getSoDienThoai())
                .taiKhoan(taiKhoan)
                .build();

        if(currentUser.getVaiTro() == Role.ADMIN){
            taiKhoan.setVaiTro(Role.TRO_LY_KHOA);
        }
        taiKhoan.setGiangVien(giangVien);
        taiKhoanRepository.save(taiKhoan);
        return giangVienMapper.toGiangVienCreationResponse(giangVienRepository.save(giangVien));

    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Override
    public void createTroLyKhoa(TroLyKhoaCreationRequest troLyKhoaCreationRequest) {
        GiangVien troLyKhoa = giangVienRepository.findById(troLyKhoaCreationRequest.getGiangVienId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.GIANG_VIEN_NOT_FOUND));
        if(troLyKhoa.getTaiKhoan().getVaiTro() == Role.TRUONG_BO_MON){
            throw new ApplicationException(ErrorCode.INVALID_TRO_LY_KHOA);
        }
        if(troLyKhoa.getTaiKhoan().getVaiTro() == Role.TRO_LY_KHOA){
            return;
        }
        troLyKhoa.getTaiKhoan().setVaiTro(Role.TRO_LY_KHOA);
        giangVienRepository.save(troLyKhoa);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public GiangVienImportResponse importGiangVien(MultipartFile file) throws IOException {
        int total = 0, ok = 0;
        List<String> errs = new ArrayList<>();

        try (InputStream in = file.getInputStream();
             XSSFWorkbook wb = new XSSFWorkbook(in)) {

            XSSFSheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();     // đọc mọi cell -> String, không mất số đầu
            Map<String,Integer> col = headerIndex(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                total++;

                try {
                    String maGV    = fmt.formatCellValue(r.getCell(col.get("Mã giảng viên"))).trim();
                    String hoTen   = fmt.formatCellValue(r.getCell(col.get("Họ tên"))).trim();
                    String sdt     = fmt.formatCellValue(r.getCell(col.get("Số điện thoại"))).trim();
                    String email   = fmt.formatCellValue(r.getCell(col.get("Email"))).trim().toLowerCase();
                    String matKhau = fmt.formatCellValue(r.getCell(col.get("Mật khẩu"))).trim();
                    String boMonTx = fmt.formatCellValue(r.getCell(col.get("Bộ môn"))).trim();
                    String hocVi   = fmt.formatCellValue(r.getCell(col.get("Học vị"))).trim();
                    String hocHam  = fmt.formatCellValue(r.getCell(col.get("Học hàm"))).trim();

                    // Lấy boMonId: nếu cột là ID thì parse, còn không thì tìm theo tên
                    Long boMonId = tryParseLong(boMonTx);
                    if (boMonId == null) {
                        boMonId = boMonRepository.findByTenBoMon((boMonTx))
                                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND))
                                .getId();
                    }

                    var req = GiangVienCreationRequest.builder()
                            .maGV(maGV)
                            .hoTen(hoTen)
                            .soDienThoai(sdt)
                            .email(email)
                            .matKhau(matKhau)
                            .hocVi(hocVi)
                            .hocHam(hocHam)
                            .boMonId(boMonId)
                            .build();

                    createGiangVien(req);  // tái dùng logic hiện có
                    ok++;

                } catch (ApplicationException ex) {
                    errs.add("Row " + (i + 1) + ": " + ex.getErrorCode().name());
                } catch (Exception ex) {
                    errs.add("Row " + (i + 1) + ": " + ex.getMessage());
                }
            }
        }

        return GiangVienImportResponse.builder()
                .totalRows(total)
                .success(ok)
                .errors(errs)
                .build();
    }

    private Map<String,Integer> headerIndex(Row header) {
        Map<String,Integer> m = new HashMap<>();
        DataFormatter fmt = new DataFormatter();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            m.put(fmt.formatCellValue(header.getCell(c)).trim(), c);
        }
        return m;
    }
    private Long tryParseLong(String s) {
        try { return Long.valueOf(s); } catch (Exception e) { return null; }
    }
    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return auth.getName();
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<GiangVienLiteResponse> getGiangVienLiteByBoMon(Long boMonId) {
        BoMon bm = boMonRepository.findById(boMonId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));
        return giangVienRepository.findByBoMon_IdOrderByHoTenAsc(bm.getId())
                .stream()
                .map(giangVienMapper::toLite)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<GiangVienResponse> getAllGiangVien(Pageable pageable) {
        Page<GiangVien> page = giangVienRepository.findAll(pageable);
        return page.map(giangVienMapper::toGiangVienResponse);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
    @Override
    public GiangVienResponse updateGiangVien(Long id, GiangVienUpdateRequest request) {
        GiangVien existingGV = giangVienRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.GIANG_VIEN_NOT_FOUND));

        TaiKhoan taiKhoan = existingGV.getTaiKhoan();

        // Check email duplication
        if (taiKhoanRepository.existsByEmail(request.getEmail())
                && !taiKhoan.getEmail().equals(request.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }

        // Validate password
        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()
                && request.getMatKhau().length() < 6) {
            throw new ApplicationException(ErrorCode.PASSWORD_INVALID);
        }

        // Update tài khoản
        taiKhoan.setEmail(request.getEmail());
        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()) {
            taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        taiKhoanRepository.save(taiKhoan);

        // Update thông tin giảng viên
        existingGV.setHoTen(request.getHoTen());
        existingGV.setSoDienThoai(request.getSoDienThoai());
        existingGV.setHocVi(request.getHocVi());
        existingGV.setHocHam(request.getHocHam());

        if (request.getBoMonId() != null) {
            BoMon bm = boMonRepository.findById(request.getBoMonId())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));
            existingGV.setBoMon(bm);
        }

        return giangVienMapper.toGiangVienResponse(giangVienRepository.save(existingGV));
    }

}

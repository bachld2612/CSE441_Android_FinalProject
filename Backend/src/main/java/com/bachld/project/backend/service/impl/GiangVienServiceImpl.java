package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.*;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.TaiKhoan;
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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIEN')")
    @Override
    public Page<SinhVienSupervisedResponse> getMySupervisedStudents(Pageable pageable) {
        String email = currentEmail();

        Long gvhdId = giangVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_A_GVHD))
                .getId();

        Page<SinhVien> page = sinhVienRepository.findByDeTai_Gvhd_Id(gvhdId, pageable);
        return page.map(sinhVienMapper::toSinhVienSupervisedResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIEN')")
    @Override
    public Page<DeTaiSinhVienApprovalResponse> getDeTaiSinhVienApproval(DeTaiState status, Pageable pageable) {
        String email = currentEmail();

        Long gvhdId = giangVienRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_A_GVHD))
                .getId();

        DeTaiState filter = (status == null) ? DeTaiState.PENDING : status;

        Page<SinhVien> page = sinhVienRepository
                .findByDeTai_Gvhd_IdAndDeTai_TrangThai(gvhdId, filter, pageable);

        return page.map(sinhVienMapper::toDeTaiSinhVienApprovalResponse);
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

}

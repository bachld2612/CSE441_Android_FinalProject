package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienImportResponse;
import com.bachld.project.backend.entity.Lop;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.SinhVienMapper;
import com.bachld.project.backend.repository.LopRepository;
import com.bachld.project.backend.repository.SinhVienRepository;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.SinhVienService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class SinhVienServiceImpl implements SinhVienService {

    SinhVienRepository sinhVienRepository;
    LopRepository lopRepository;
    PasswordEncoder passwordEncoder;
    SinhVienMapper sinhVienMapper;
    TaiKhoanRepository taiKhoanRepository;
    CloudinaryServiceImpl cloudinaryService;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public SinhVienCreationResponse createSinhVien(SinhVienCreationRequest request) {

        if(taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }
        if(sinhVienRepository.existsByMaSV(request.getMaSV())) {
            throw new ApplicationException(ErrorCode.MA_SV_EXISTED);
        }

        Lop lop = lopRepository.findById(request.getLopId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.LOP_NOT_FOUND));

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .vaiTro(Role.SINH_VIEN)
                .build();

        SinhVien sinhVien = SinhVien.builder()
                .hoTen(request.getHoTen())
                .maSV(request.getMaSV())
                .kichHoat(true)
                .lop(lop)
                .taiKhoan(taiKhoan)
                .soDienThoai(request.getSoDienThoai())
                .build();
        taiKhoan.setSinhVien(sinhVien);
        taiKhoanRepository.save(taiKhoan);
        return sinhVienMapper.toSinhVienCreationResponse(sinhVienRepository.save(sinhVien));

    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public SinhVienImportResponse importSinhVien(MultipartFile file) throws IOException {
        int total = 0, ok = 0;
        List<String> errs = new ArrayList<>();

        try (InputStream in = file.getInputStream();
             XSSFWorkbook wb = new XSSFWorkbook(in)) {

            XSSFSheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            Map<String,Integer> col = headerIndex(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                total++;

                try {
                    String maSV       = fmt.formatCellValue(r.getCell(col.get("Mã Sinh Viên")));
                    String hoTen      = fmt.formatCellValue(r.getCell(col.get("Họ tên")));
                    String sdt        = fmt.formatCellValue(r.getCell(col.get("Số điện thoại")));
                    String email      = fmt.formatCellValue(r.getCell(col.get("Email")));
                    String matKhau    = fmt.formatCellValue(r.getCell(col.get("Mật khẩu")));
                    String lopText    = fmt.formatCellValue(r.getCell(col.get("Lớp")));

                    Long lopId = tryParseLong(lopText);
                    if (lopId == null) {
                        lopId = lopRepository.findByTenLop((lopText))
                                .orElseThrow(() -> new ApplicationException(ErrorCode.LOP_NOT_FOUND))
                                .getId();
                    }

                    SinhVienCreationRequest req = SinhVienCreationRequest.builder()
                            .maSV(maSV)
                            .hoTen(hoTen)
                            .soDienThoai(sdt)
                            .email(email)
                            .matKhau(matKhau)
                            .lopId(lopId)
                            .build();

                    createSinhVien(req); // TÁI DÙNG logic hiện có
                    ok++;

                } catch (ApplicationException ex) {
                    errs.add("Row " + (i+1) + ": " + ex.getErrorCode().name());
                } catch (Exception ex) {
                    errs.add("Row " + (i+1) + ": " + ex.getMessage());
                }
            }
        }
        return SinhVienImportResponse.builder()
                .totalRows(total)
                .success(ok)
                .errors(errs)
                .build();
    }

    private Map<String,Integer> headerIndex(Row header) {
        Map<String,Integer> m = new HashMap<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            m.put(new DataFormatter().formatCellValue(header.getCell(c)).trim(), c);
        }
        return m;
    }
    private Long tryParseLong(String s) {
        try { return Long.valueOf(s); } catch (Exception e) { return null; }
    }
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return auth.getName();
    }

    private String upload(org.springframework.web.multipart.MultipartFile file) {
        try { return cloudinaryService.upload(file); }
        catch (Exception e) { throw new ApplicationException(ErrorCode.UPLOAD_FILE_FAILED); }
    }
}

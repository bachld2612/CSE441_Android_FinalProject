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
import com.bachld.project.backend.repository.BoMonRepository;
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

import java.util.List;


//them danh sách log đề cương của sinh viên
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DeCuongServiceImpl implements DeCuongService {

    DeCuongRepository deCuongRepository;
    DeTaiRepository deTaiRepository;
    GiangVienRepository giangVienRepository;
    BoMonRepository boMonRepository;
    DeCuongMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongResponse submitDeCuong(Long deTaiId, String fileUrl) {
        DeTai deTai = deTaiRepository.findById(deTaiId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        if (deTai.getTrangThai() != DeTaiState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_TAI_NOT_ACCEPTED);
        }

        String email = currentUsername();
        boolean isOwner = deTai.getSinhVienThucHien() != null
                && deTai.getSinhVienThucHien().getTaiKhoan() != null
                && email.equalsIgnoreCase(deTai.getSinhVienThucHien().getTaiKhoan().getEmail());
        if (!isOwner) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        DeCuong dc = deCuongRepository.findByDeTai_Id(deTai.getId())
                .map(existing -> {
                    if (existing.getTrangThai() == DeCuongState.ACCEPTED) {
                        throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
                    }
                    existing.setDeCuongUrl(fileUrl);
                    existing.setTrangThai(DeCuongState.PENDING);
                    existing.setSoLanNop(existing.getSoLanNop() + 1);
//                    existing.setNhanXet(null);
                    return existing;
                })
                .orElseGet(() -> {
                    DeCuong created = new DeCuong();
                    created.setDeTai(deTai);
                    created.setDeCuongUrl(fileUrl);
                    created.setTrangThai(DeCuongState.PENDING);
                    created.setSoLanNop(1);
//                    created.setNhanXet(null);
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
//                dc.setNhanXet(reason.trim());
            }
        } else {
            // Reject -> bắt buộc có lý do
            if (reason == null || reason.isBlank()) {
                // dùng mã 1219 như đã thống nhất
                throw new ApplicationException(ErrorCode.DE_CUONG_REASON_REQUIRED);
            }
            dc.setTrangThai(DeCuongState.CANCELED);
//            dc.setNhanXet(reason.trim());
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

    @PreAuthorize("hasAuthority('SCOPE_TRUONG_BO_MON')")
    @Override
    public Page<DeCuongResponse> getAcceptedForTBM(Pageable pageable) {
        Long bmId = currentTBMBoMonId();
        return deCuongRepository
                .findByTrangThaiAndDeTai_BoMonQuanLy_Id(DeCuongState.ACCEPTED, bmId, pageable)
                .map(mapper::toResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRUONG_BO_MON')")
    @Override
    public byte[] exportAcceptedForTBMAsExcel() {
        Long bmId = currentTBMBoMonId();
        List<DeCuong> list = deCuongRepository
                .findByTrangThaiAndDeTai_BoMonQuanLy_Id(DeCuongState.ACCEPTED, bmId);

        try (var wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = wb.createSheet("DeCuongAccepted");

            // Header style
            var headerStyle = wb.createCellStyle();
            var bold = wb.createFont(); bold.setBold(true);
            headerStyle.setFont(bold);

            // ⚠️ Thêm "Bộ môn quản lý" giữa "Tên đề tài" và "File URL"
            String[] headers = {"Mã sinh viên","Họ và tên","Lớp","GVHD","Tên đề tài","Bộ môn quản lý","File URL"};
            var row0 = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var c = row0.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data
            int r = 1;
            for (DeCuong dc : list) {
                var dt = dc.getDeTai();
                var sv = dt != null ? dt.getSinhVienThucHien() : null;
                var lop = (sv != null && sv.getLop() != null) ? sv.getLop().getTenLop() : "";
                var gv  = (dt != null && dt.getGvhd() != null) ? dt.getGvhd().getHoTen() : "";
                var bm  = (dt != null && dt.getBoMonQuanLy() != null) ? dt.getBoMonQuanLy().getTenBoMon() : "";

                var row = sheet.createRow(r++);
                row.createCell(0).setCellValue(sv != null ? nvl(sv.getMaSV()) : "");
                row.createCell(1).setCellValue(sv != null ? nvl(sv.getHoTen()) : "");
                row.createCell(2).setCellValue(nvl(lop));
                row.createCell(3).setCellValue(nvl(gv));
                row.createCell(4).setCellValue(dt != null ? nvl(dt.getTenDeTai()) : "");
                row.createCell(5).setCellValue(nvl(bm));                    // ← Bộ môn quản lý (cột mới)
                row.createCell(6).setCellValue(nvl(dc.getDeCuongUrl()));    // File URL
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            try (var bos = new java.io.ByteArrayOutputStream()) {
                wb.write(bos);
                return bos.toByteArray();
            }
        } catch (java.io.IOException e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static String nvl(String s) { return s == null ? "" : s; }



    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Long currentTBMBoMonId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        GiangVien gv = giangVienRepository.findByTaiKhoan_EmailIgnoreCase(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));
        if (gv.getBoMon() == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }
        return gv.getBoMon().getId(); // TBM thuộc bộ môn nào thì chỉ xem bộ môn đó
    }

}
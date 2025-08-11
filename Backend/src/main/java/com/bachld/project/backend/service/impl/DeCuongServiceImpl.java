package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.entity.DeCuong;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.enums.CongViec;
import com.bachld.project.backend.enums.DeCuongState;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DeCuongMapper;
import com.bachld.project.backend.repository.DeCuongRepository;
import com.bachld.project.backend.repository.DeTaiRepository;
import com.bachld.project.backend.repository.GiangVienRepository;
import com.bachld.project.backend.repository.ThoiGianThucHienRepository;
import com.bachld.project.backend.service.DeCuongService;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DeCuongServiceImpl implements DeCuongService {

    DeCuongRepository deCuongRepository;
    DeTaiRepository deTaiRepository;
    GiangVienRepository giangVienRepository;
    DeCuongMapper mapper;

    ThoiGianThucHienRepository thoiGianThucHienRepository;
    TimeGatekeeper timeGatekeeper;

    private static final ZoneId ZONE_BKK = ZoneId.of("Asia/Bangkok");

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongResponse submitDeCuong(Long deTaiId, String fileUrl) {
        DeTai deTai = deTaiRepository.findById(deTaiId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        if (deTai.getTrangThai() != DeTaiState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_TAI_NOT_ACCEPTED);
        }

        DotBaoVe dot = deTai.getDotBaoVe();
        if (dot == null) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW);
        }

        // time gate: chỉ cho nộp khi trong khoảng NỘP_ĐỀ_CƯƠNG của ĐỢT của đề tài
        timeGatekeeper.assertWithinWindow(CongViec.NOP_DE_CUONG, dot);

        // kiểm tra SV sở hữu
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
                    existing.setNhanXet(null);
                    return existing;
                })
                .orElseGet(() -> {
                    DeCuong created = new DeCuong();
                    created.setDeTai(deTai);
                    created.setDeCuongUrl(fileUrl);
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
        String email = currentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_EmailIgnoreCase(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        DeCuong dc = deCuongRepository.findById(deCuongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_CUONG_NOT_FOUND));

        DeTai deTai = dc.getDeTai();
        if (deTai == null || deTai.getDotBaoVe() == null) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW);
        }

        // duyệt chỉ trong khoảng NỘP_ĐỀ_CƯƠNG của đợt đề tài
        timeGatekeeper.assertWithinWindow(CongViec.NOP_DE_CUONG, deTai.getDotBaoVe());

        // chỉ GVHD được duyệt
        if (deTai.getGvhd() == null || !deTai.getGvhd().getId().equals(gv.getId())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        if (dc.getTrangThai() == DeCuongState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
        }
        if (dc.getTrangThai() == DeCuongState.CANCELED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_REJECTED);
        }
        if (dc.getTrangThai() != DeCuongState.PENDING) {
            throw new ApplicationException(ErrorCode.OUTLINE_NOT_PENDING);
        }

        if (approve) {
            dc.setTrangThai(DeCuongState.ACCEPTED);
            if (reason != null && !reason.isBlank()) {
                dc.setNhanXet(reason.trim());
            }
        } else {
            if (reason == null || reason.isBlank()) {
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
        // 1) Lấy danh sách dot đang mở NOP_DE_CUONG hôm nay
        List<Long> activeDotIds = activeSubmissionDotIdsToday();

        // 2) Phân quyền: GV chỉ xem SV mình hướng dẫn
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        boolean isGV = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SCOPE_GIANG_VIEN"));

        Page<DeCuong> page = isGV
                ? deCuongRepository
                .findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCaseAndDeTai_DotBaoVe_IdIn(email, activeDotIds, pageable)
                : deCuongRepository
                .findByDeTai_DotBaoVe_IdIn(activeDotIds, pageable);

        return page.map(mapper::toResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRUONG_BO_MON')")
    @Override
    public Page<DeCuongResponse> getAcceptedForTBM(Pageable pageable) {
        // 1) Lấy danh sách dot đang mở NOP_DE_CUONG hôm nay
        List<Long> activeDotIds = activeSubmissionDotIdsToday();

        // 2) Lấy bộ môn của TBM
        Long bmId = currentTBMBoMonId();

        // 3) Trả về chỉ các đề cương đã ACCEPTED trong các đợt đang mở
        return deCuongRepository
                .findByTrangThaiAndDeTai_BoMonQuanLy_IdAndDeTai_DotBaoVe_IdIn(
                        DeCuongState.ACCEPTED, bmId, activeDotIds, pageable)
                .map(mapper::toResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRUONG_BO_MON')")
    @Override
    public byte[] exportAcceptedForTBMAsExcel() {
        // 1) Lấy danh sách dot đang mở trong NOP_DE_CUONG
        List<Long> activeDotIds = activeSubmissionDotIdsToday();

        // 2) Lấy bộ môn của TBM
        Long bmId = currentTBMBoMonId();

        // 3) Dataset xuất file = đúng dataset của getAcceptedForTBM (không lọc updatedAt thủ công nữa)
        List<DeCuong> list = deCuongRepository
                .findByTrangThaiAndDeTai_BoMonQuanLy_IdAndDeTai_DotBaoVe_IdIn(
                        DeCuongState.ACCEPTED, bmId, activeDotIds);

        try (var wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = wb.createSheet("danh_sach_de_cuong_duoc_duyet");

            var headerStyle = wb.createCellStyle();
            var bold = wb.createFont(); bold.setBold(true);
            headerStyle.setFont(bold);

            var linkStyle = wb.createCellStyle();
            var linkFont = wb.createFont();
            linkFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
            linkFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.BLUE.getIndex());
            linkStyle.setFont(linkFont);

            var helper = wb.getCreationHelper();

            String[] headers = {"Mã sinh viên","Họ và tên","Lớp","GVHD","Tên đề tài","Bộ môn quản lý","File URL"};
            var row0 = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var c = row0.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int r = 1;
            for (DeCuong dc : list) {
                var dt = dc.getDeTai();
                var sv = dt != null ? dt.getSinhVienThucHien() : null;
                var lop = (sv != null && sv.getLop() != null) ? sv.getLop().getTenLop() : "";
                var gv  = (dt != null && dt.getGvhd() != null) ? dt.getGvhd().getHoTen() : "";
                var bm  = (dt != null && dt.getBoMonQuanLy() != null) ? dt.getBoMonQuanLy().getTenBoMon() : "";
                var fileUrl = dc.getDeCuongUrl();

                var row = sheet.createRow(r++);
                row.createCell(0).setCellValue(sv != null ? nvl(sv.getMaSV()) : "");
                row.createCell(1).setCellValue(sv != null ? nvl(sv.getHoTen()) : "");
                row.createCell(2).setCellValue(nvl(lop));
                row.createCell(3).setCellValue(nvl(gv));
                row.createCell(4).setCellValue(dt != null ? nvl(dt.getTenDeTai()) : "");
                row.createCell(5).setCellValue(nvl(bm));

                var linkCell = row.createCell(6);
                if (fileUrl != null && !fileUrl.isBlank()) {
                    String address = toClickableUrl(fileUrl);
                    var hyperlink = helper.createHyperlink(
                            address.startsWith("http") || address.startsWith("file:")
                                    ? org.apache.poi.common.usermodel.HyperlinkType.URL
                                    : org.apache.poi.common.usermodel.HyperlinkType.FILE
                    );
                    hyperlink.setAddress(address);
                    linkCell.setCellValue("Mở file");
                    linkCell.setHyperlink(hyperlink);
                    linkCell.setCellStyle(linkStyle);
                } else {
                    linkCell.setCellValue("");
                }
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                wb.write(bos);
                return bos.toByteArray();
            }
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // ===== helpers =====

    /** Lấy danh sách dot_bao_ve_id đang mở NOP_DE_CUONG hôm nay. Nếu không có → ném lỗi **/
    private List<Long> activeSubmissionDotIdsToday() {
        LocalDate today = LocalDate.now(ZONE_BKK);
        List<ThoiGianThucHien> open = thoiGianThucHienRepository
                .findAllByCongViecAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
                        CongViec.NOP_DE_CUONG, today, today);

        if (open.isEmpty()) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_REVIEW_LIST);
        }

        return open.stream()
                .map(t -> t.getDotBaoVe().getId())
                .distinct()
                .toList();
    }

    private static String toClickableUrl(String input) {
        String s = input.trim();
        if (s.startsWith("http://") || s.startsWith("https://") || s.startsWith("file:")) {
            return s;
        }
        try {
            java.nio.file.Path p = java.nio.file.Paths.get(s);
            return p.toUri().toString();
        } catch (Exception e) {
            return s;
        }
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

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
        return gv.getBoMon().getId();
    }
}

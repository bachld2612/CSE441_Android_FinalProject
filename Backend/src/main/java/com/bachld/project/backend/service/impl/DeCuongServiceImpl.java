package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.decuong.DeCuongUploadRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongLogResponse;
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
import com.bachld.project.backend.repository.*;
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
import com.bachld.project.backend.service.CloudinaryService;

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
    DeCuongLogRepository deCuongLogRepository;
    ThoiGianThucHienRepository thoiGianThucHienRepository;
    TimeGatekeeper timeGatekeeper;

    private static final ZoneId ZONE_BKK = ZoneId.of("Asia/Bangkok");
    private final CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongResponse submitDeCuong(DeCuongUploadRequest request) {
        // 1. Lấy đề tài
        DeTai deTai = deTaiRepository.findById(request.getDeTaiId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_TAI_NOT_FOUND));

        // 2. Kiểm tra trạng thái đề tài
        if (deTai.getTrangThai() != DeTaiState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_TAI_NOT_ACCEPTED);
        }

        DotBaoVe dot = deTai.getDotBaoVe();
        if (dot == null) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW);
        }

        // 3. Chỉ cho nộp trong mốc NỘP_ĐỀ_CƯƠNG
        timeGatekeeper.assertWithinWindow(CongViec.NOP_DE_CUONG, dot);

        // 4. Xác thực sinh viên chủ sở hữu
        String email = currentUsername();
        boolean isOwner = deTai.getSinhVienThucHien() != null
                && deTai.getSinhVienThucHien().getTaiKhoan() != null
                && email.equalsIgnoreCase(deTai.getSinhVienThucHien().getTaiKhoan().getEmail());
        if (!isOwner) {
            throw new ApplicationException(ErrorCode.ACCESS_DENIED);
        }

        // 5. Xác định URL (upload hoặc dùng trực tiếp)
        String tmpUrl;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            // Upload lên Cloudinary
            tmpUrl = cloudinaryService.upload(request.getFile());
        } else if (request.getFileUrl() != null && !request.getFileUrl().isBlank()) {
            // Dùng URL có sẵn
            tmpUrl = request.getFileUrl().trim();
        } else {
            throw new ApplicationException(ErrorCode.FILE_URL_EMPTY);
        }

        final String finalUrl = tmpUrl; // biến final để dùng trong lambda

        // 6. Tạo mới hoặc cập nhật DeCuong
        DeCuong dc = deCuongRepository.findByDeTai_Id(deTai.getId())
                .map(existing -> {
                    if (existing.getTrangThai() == DeCuongState.ACCEPTED) {
                        throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
                    }
                    existing.setDeCuongUrl(finalUrl);
                    existing.setTrangThai(DeCuongState.PENDING);
                    existing.setSoLanNop(existing.getSoLanNop() + 1);
                    return existing;
                })
                .orElseGet(() -> {
                    DeCuong created = new DeCuong();
                    created.setDeTai(deTai);
                    created.setDeCuongUrl(finalUrl);
                    created.setTrangThai(DeCuongState.PENDING);
                    created.setSoLanNop(1);
                    return created;
                });

        // 7. Lưu DB và trả response
        return mapper.toResponse(deCuongRepository.save(dc));
    }


    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    @Override
    public DeCuongLogResponse viewDeCuongLog() {
        String email = currentUsername();

        // Tìm đề cương theo tài khoản SV (mỗi SV tối đa 1 đề tài nhờ UNIQUE)
        DeCuong dc = deCuongRepository
                .findByDeTai_SinhVienThucHien_TaiKhoan_EmailIgnoreCase(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_CUONG_NOT_FOUND));

        // Lấy toàn bộ log bị từ chối (được ghi khi GV reject)
        var logs = deCuongLogRepository.findByDeCuong_IdOrderByCreatedAtAsc(dc.getId());

        DeCuongLogResponse res = new DeCuongLogResponse();
        res.setFileUrlMoiNhat(dc.getDeCuongUrl());
        res.setNgayNopGanNhat(dc.getUpdatedAt());
        res.setTongSoLanNop(dc.getSoLanNop());
        res.setCacNhanXetTuChoi(
                logs.stream()
                        .filter(l -> l.getNhanXet() != null && !l.getNhanXet().isBlank())
                        .map(l -> new DeCuongLogResponse.RejectNote(l.getCreatedAt(), l.getNhanXet()))
                        .toList()
        );
        return res;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN', 'SCOPE_TRUONG_BO_MON')")
    @Override
    public DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve, String reason) {
        String email = currentUsername();
        GiangVien gv = giangVienRepository.findByTaiKhoan_EmailIgnoreCase(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ACCESS_DENIED));

        DeCuong dc = deCuongRepository.findById(deCuongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DE_CUONG_NOT_FOUND));

        DeTai deTai = dc.getDeTai();
        if (deTai == null || deTai.getDotBaoVe() == null) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW);
        }

        // Chỉ trong mốc NỘP_ĐỀ_CƯƠNG của đúng đợt của đề tài
        timeGatekeeper.assertWithinWindow(CongViec.NOP_DE_CUONG, deTai.getDotBaoVe());

        // Chỉ GVHD
        if (deTai.getGvhd() == null || !deTai.getGvhd().getId().equals(gv.getId())) {
            throw new ApplicationException(ErrorCode.ACCESS_DENIED);
        }

        if (dc.getTrangThai() == DeCuongState.ACCEPTED) {
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_APPROVED);
        }
        if (dc.getTrangThai() == DeCuongState.CANCELED) {
            // Đã bị từ chối thì SV phải nộp lại (PENDING) rồi mới xét tiếp
            throw new ApplicationException(ErrorCode.DE_CUONG_ALREADY_REJECTED);
        }
        if (dc.getTrangThai() != DeCuongState.PENDING) {
            throw new ApplicationException(ErrorCode.OUTLINE_NOT_PENDING);
        }

        if (approve) {
            dc.setTrangThai(DeCuongState.ACCEPTED);
        } else {
            if (reason == null || reason.isBlank()) {
                throw new ApplicationException(ErrorCode.DE_CUONG_REASON_REQUIRED);
            }
            // Ghi log nhận xét từ chối
            var log = new com.bachld.project.backend.entity.DeCuongLog();
            log.setDeCuong(dc);
            log.setNhanXet(reason.trim());
            deCuongLogRepository.save(log);

            dc.setTrangThai(DeCuongState.CANCELED);
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
                .anyMatch(a -> a.getAuthority().equals("SCOPE_GIANG_VIEN")
                        || a.getAuthority().equals("SCOPE_TRUONG_BO_MON"));


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
                .orElseThrow(() -> new ApplicationException(ErrorCode.ACCESS_DENIED));
        if (gv.getBoMon() == null) {
            throw new ApplicationException(ErrorCode.ACCESS_DENIED);
        }
        return gv.getBoMon().getId();
    }
}

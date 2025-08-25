package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.hoidong.HoiDongCreateRequest;
import com.bachld.project.backend.dto.response.hoidong.AddSinhVienToHoiDongResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.entity.*;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.enums.HoiDongRole;
import com.bachld.project.backend.enums.HoiDongType;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.HoiDongMapper;
import com.bachld.project.backend.repository.*;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.HoiDongService;
import com.bachld.project.backend.util.TimeGatekeeper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HoiDongServiceImpl implements HoiDongService {

    HoiDongRepository hoiDongRepository;
    HoiDongMapper hoiDongMapper;

    DeTaiRepository deTaiRepository;
    DotBaoVeRepository dotBaoVeRepository;
    GiangVienRepository giangVienRepository;
    DotBaoVeGiangVienRepository dotBaoVeGiangVienRepository;
    private final ThanhVienHoiDongRepository thanhVienHoiDongRepository;
    CloudinaryService cloudinaryService;
    TimeGatekeeper timeGatekeeper;

    @PersistenceContext
    EntityManager em;

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<HoiDongListItemResponse> getHoiDongsDangDienRa(String keyword, HoiDongType type, Pageable pageable) {
        LocalDate today = LocalDate.now();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        DotBaoVe dotBaoVe = timeGatekeeper.getCurrentDotBaoVe();

        Page<HoiDong> page;
        if (hasKeyword) {
            page = hoiDongRepository.findHoiDongByDotBaoVeAndTenHoiDongContainingIgnoreCase(dotBaoVe, keyword, pageable);
        }else {
            page = hoiDongRepository.findHoiDongByDotBaoVe(dotBaoVe, pageable);
        }

        return page.map(hoiDongMapper::toListItem);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public HoiDongDetailResponse getHoiDongDetail(Long hoiDongId) {
        HoiDong hd = hoiDongRepository.fetchDetail(hoiDongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.HOI_DONG_NOT_FOUND));
        return hoiDongMapper.toDetail(hd);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<HoiDongListItemResponse> getTatCaHoiDongByDot(Long dotBaoVeId, String keyword, HoiDongType type, Pageable pageable) {
        if (dotBaoVeId == null) {
            throw new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND);
        }

        boolean hasKeyword = keyword != null && !keyword.isBlank();

        Page<HoiDong> page;
        if (type != null && hasKeyword) {
            page = hoiDongRepository
                    .findByDotBaoVe_IdAndLoaiHoiDongAndTenHoiDongContainingIgnoreCase(dotBaoVeId, type, keyword, pageable);
        } else if (type != null) {
            page = hoiDongRepository
                    .findByDotBaoVe_IdAndLoaiHoiDong(dotBaoVeId, type, pageable);
        } else if (hasKeyword) {
            page = hoiDongRepository
                    .findByDotBaoVe_IdAndTenHoiDongContainingIgnoreCase(dotBaoVeId, keyword, pageable);
        } else {
            page = hoiDongRepository
                    .findByDotBaoVe_Id(dotBaoVeId, pageable);
        }

        return page.map(hoiDongMapper::toListItem);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    @Transactional
    public HoiDongDetailResponse createHoiDong(HoiDongCreateRequest request) {
        DotBaoVe dot = dotBaoVeRepository.findById(request.getDotBaoVeId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND));

        // validate time...
        if (request.getThoiGianBatDau().isAfter(request.getThoiGianKetThuc()))
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        if (request.getThoiGianBatDau().isBefore(dot.getThoiGianBatDau())
                || request.getThoiGianKetThuc().isAfter(dot.getThoiGianKetThuc()))
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);

        var lecturers = request.getLecturers() == null ? List.<HoiDongCreateRequest.LecturerItem>of()
                : request.getLecturers();

        if (request.getLoaiHoiDong() == HoiDongType.PEER_REVIEW) {
            if (lecturers.size() != 1)
                throw new ApplicationException(ErrorCode.INVALID_HOI_DONG_TYPE_CONFIG);
        } else if (lecturers.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_HOI_DONG_TYPE_CONFIG);
        }

        HoiDong hd = new HoiDong();
        hd.setTenHoiDong(request.getTenHoiDong());
        hd.setThoiGianBatDau(request.getThoiGianBatDau());
        hd.setThoiGianKetThuc(request.getThoiGianKetThuc());
        hd.setLoaiHoiDong(request.getLoaiHoiDong());
        hd.setDotBaoVe(dot);
        hd.setDeTaiSet(new HashSet<>());
        hd.setThanhVienHoiDongSet(new HashSet<>());

        HoiDong saved = hoiDongRepository.save(hd);

        // tạo và lưu các thành viên
        List<ThanhVienHoiDong> members = new ArrayList<>();
        for (var li : lecturers) {
            GiangVien gv = giangVienRepository.findById(li.getGiangVienId())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.GIANGVIEN_LECTURER_NOT_FOUND));

            DotBaoVeGiangVien dvgv = dotBaoVeGiangVienRepository
                    .findByDotBaoVe_IdAndGiangVien_Id(dot.getId(), gv.getId())
                    .orElseGet(() -> {
                        DotBaoVeGiangVien x = new DotBaoVeGiangVien();
                        x.setDotBaoVe(dot);
                        x.setGiangVien(gv);
                        return dotBaoVeGiangVienRepository.save(x);
                    });

            ThanhVienHoiDong tv = new ThanhVienHoiDong();
            tv.setHoiDong(saved);
            tv.setDotBaoVeGiangVien(dvgv);
            tv.setChucVu(request.getLoaiHoiDong() == HoiDongType.PEER_REVIEW
                    ? HoiDongRole.CHAIR
                    : (li.getRole() != null ? li.getRole() : HoiDongRole.EXAMINER));
            members.add(tv);
        }
        thanhVienHoiDongRepository.saveAll(members);

        em.flush();
        em.clear();

        HoiDong full = hoiDongRepository.fetchDetail(saved.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.HOI_DONG_NOT_FOUND));

        return hoiDongMapper.toDetail(full);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public AddSinhVienToHoiDongResponse importSinhVienToHoiDong(Long hoiDongId, MultipartFile excelFile) {
        HoiDong hd = hoiDongRepository.findById(hoiDongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.HOI_DONG_NOT_FOUND));
        DotBaoVe dot = hd.getDotBaoVe();

        Set<DeTai> newSet = new HashSet<>();
        int success = 0;

        List<AddSinhVienToHoiDongResponse.FailureItem> failures = new ArrayList<>();
        List<ImportLogRow> logs = new ArrayList<>();

        try (InputStream is = excelFile.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;

                String maSv  = fmt.formatCellValue(r.getCell(0)).trim();  // A: Mã SV
                String tenDt = fmt.formatCellValue(r.getCell(1)).trim();  // B: Tên đề tài

                if (maSv.isEmpty() || tenDt.isEmpty()) {
                    String reason = "Thiếu dữ liệu";
                    failures.add(fail(maSv, tenDt, reason));
                    logs.add(new ImportLogRow(maSv, tenDt, false, reason));
                    continue;
                }

                List<DeTai> candidates = deTaiRepository
                        .findBySinhVienThucHien_MaSVIgnoreCaseAndDotBaoVe_IdAndTrangThai(
                                maSv, dot.getId(), DeTaiState.ACCEPTED);

                if (candidates.isEmpty()) {
                    String reason = "SV chưa có đề tài được duyệt trong đợt này";
                    failures.add(fail(maSv, tenDt, reason));
                    logs.add(new ImportLogRow(maSv, tenDt, false, reason));
                    continue;
                }

                String want = norm(tenDt);
                DeTai dt = null;
                for (DeTai d : candidates) {
                    if (norm(d.getTenDeTai()).equals(want)) { dt = d; break; }
                }

                if (dt == null) {
                    String reason = "Tên đề tài trong file không khớp hệ thống";
                    failures.add(fail(maSv, tenDt, reason));
                    logs.add(new ImportLogRow(maSv, tenDt, false, reason));
                    continue;
                }

                boolean conflicted = hoiDongRepository
                        .existsByDotBaoVe_IdAndLoaiHoiDongAndDeTaiSet_IdAndIdNot(
                                dot.getId(), hd.getLoaiHoiDong(), dt.getId(), hd.getId()
                        );

                if (conflicted) {
                    String loaiText = (hd.getLoaiHoiDong() == HoiDongType.PEER_REVIEW) ? "phản biện" : "bảo vệ";

                    String extra = hoiDongRepository
                            .findFirstByDotBaoVe_IdAndLoaiHoiDongAndDeTaiSet_IdAndIdNot(
                                    dot.getId(), hd.getLoaiHoiDong(), dt.getId(), hd.getId()
                            )
                            .map(x -> " (" + x.getTenHoiDong() + ")")
                            .orElse("");

                    String reason = "Đã thuộc HĐ " + loaiText + " khác trong đợt này" + extra;
                    failures.add(fail(maSv, tenDt, reason));
                    logs.add(new ImportLogRow(maSv, tenDt, false, reason));
                    continue;
                }

                LocalDate s = hd.getThoiGianBatDau(), e = hd.getThoiGianKetThuc();
                if (s.isBefore(dot.getThoiGianBatDau()) || e.isAfter(dot.getThoiGianKetThuc())) {
                    String reason = "Thời gian hội đồng không thuộc đợt";
                    failures.add(fail(maSv, tenDt, reason));
                    logs.add(new ImportLogRow(maSv, tenDt, false, reason));
                    continue;
                }

                // OK
                newSet.add(dt);
                success++;
                // thành công: để trống lý do
                logs.add(new ImportLogRow(maSv, tenDt, true, null));
            }
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.INVALID_VALIDATION);
        }

        if (success > 0) {
            if (hd.getDeTaiSet() == null) hd.setDeTaiSet(new HashSet<>());
            else hd.getDeTaiSet().clear();
            hd.getDeTaiSet().addAll(newSet);
            hoiDongRepository.save(hd);
        }

        // Luôn tạo log Excel
        String logUrl;
        try {
            File log = generateImportLogExcel(logs);
            logUrl = cloudinaryService.upload(log);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        return AddSinhVienToHoiDongResponse.builder()
                .totalRecords(success + failures.size())
                .successCount(success)
                .failureCount(failures.size())
                .failureItems(failures)
                .logFileUrl(logUrl)
                .build();
    }

    private AddSinhVienToHoiDongResponse.FailureItem fail(String maSv, String ten, String reason) {
        return AddSinhVienToHoiDongResponse.FailureItem.builder()
                .maSinhVien(maSv)
                .tenDeTai(ten)
                .reason(reason)
                .build();
    }

    private File generateImportLogExcel(List<ImportLogRow> rows) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sh = wb.createSheet("Ket qua import");

        Row h = sh.createRow(0);
        h.createCell(0).setCellValue("Mã sinh viên");
        h.createCell(1).setCellValue("Tên đề tài");
        h.createCell(2).setCellValue("Kết quả");
        h.createCell(3).setCellValue("Lý do");

        int r = 1;
        for (ImportLogRow it : rows) {
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(it.maSinhVien);
            row.createCell(1).setCellValue(it.tenDeTai);
            row.createCell(2).setCellValue(it.success ? "THÀNH CÔNG" : "THẤT BẠI");
            row.createCell(3).setCellValue(it.success ? "" : (it.reason == null ? "" : it.reason));
        }

        for (int i = 0; i <= 3; i++) sh.autoSizeColumn(i);

        File tmp = File.createTempFile("import-hoidong-log-", ".xlsx");
        try (FileOutputStream os = new FileOutputStream(tmp)) {
            wb.write(os);
        }
        wb.close();
        return tmp;
    }

    private static String norm(String s) {
        if (s == null) return "";
        s = s.replace('\u00A0', ' ');          // NBSP -> space
        s = s.replaceAll("\\s+", " ").trim();  // gộp khoảng trắng
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");       // bỏ dấu
        return n.toLowerCase(java.util.Locale.ROOT);
    }

    private static class ImportLogRow {
        public final String maSinhVien;
        public final String tenDeTai;
        public final boolean success;
        public final String reason; // null/"" nếu success

        ImportLogRow(String maSinhVien, String tenDeTai, boolean success, String reason) {
            this.maSinhVien = maSinhVien;
            this.tenDeTai = tenDeTai;
            this.success = success;
            this.reason = reason;
        }
    }
}

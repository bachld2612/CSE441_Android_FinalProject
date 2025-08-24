package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.dotbaove.AddSinhVienToDotBaoVeRequest;
import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.AddSinhVienToDotBaoVeResponse;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DotBaoVeMapper;
import com.bachld.project.backend.repository.DeTaiRepository;
import com.bachld.project.backend.repository.DotBaoVeRepository;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.DotBaoVeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DotBaoVeServiceImpl implements DotBaoVeService {

    DotBaoVeRepository dotBaoVeRepository;
    DotBaoVeMapper dotBaoVeMapper;
    DeTaiRepository deTaiRepository;
    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public DotBaoVeResponse createDotBaoVe(DotBaoVeRequest request) {

        validateDotBaoVeTime(request);
        return dotBaoVeMapper.toDotBaoVeResponse(dotBaoVeRepository.save(dotBaoVeMapper.toDotBaoVe(request)));

    }
    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public DotBaoVeResponse updateDotBaoVe(DotBaoVeRequest request, Long dotBaoVeId) {
        DotBaoVe dotBaoVe = dotBaoVeRepository.findById(dotBaoVeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND));
        if(dotBaoVeRepository.existsByTenDotBaoVeAndIdNot(request.getTenDotBaoVe(), dotBaoVeId)){
            throw new ApplicationException(ErrorCode.DUPLICATED_DOT_BAO_VE);
        }
        if (request.getThoiGianBatDau().isAfter(request.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
        if(request.getThoiGianBatDau().getYear() != request.getNamBatDau()
                && request.getThoiGianBatDau().getYear() != request.getNamKetThuc()){
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
        dotBaoVeMapper.updateDotBaoVeFromDto(request, dotBaoVe);
        return dotBaoVeMapper.toDotBaoVeResponse(dotBaoVeRepository.save(dotBaoVe));
    }

    private void validateDotBaoVeTime(DotBaoVeRequest request) {
        if(dotBaoVeRepository.existsByTenDotBaoVe(request.getTenDotBaoVe())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_DOT_BAO_VE);
        }
        if (request.getThoiGianBatDau().isAfter(request.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
        if(request.getThoiGianBatDau().getYear() != request.getNamBatDau()
                && request.getThoiGianBatDau().getYear() != request.getNamKetThuc()){
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public void deleteDotBaoVe(Long id) {

        dotBaoVeRepository.deleteById(id);

    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public Page<DotBaoVeResponse> findAllDotBaoVe(Pageable pageable) {
        Page<DotBaoVe> page = dotBaoVeRepository.findAll(pageable);
        return page.map(dotBaoVeMapper::toDotBaoVeResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public AddSinhVienToDotBaoVeResponse addSinhVienToDotBaoVe(AddSinhVienToDotBaoVeRequest request) throws IOException {
        List<AddSinhVienToDotBaoVeResponse.FailureItem> failures = new ArrayList<>();
        int success = 0;

        try (InputStream is = request.getDataFile().getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Tìm đợt bảo vệ
            DotBaoVe dotBaoVe = dotBaoVeRepository.findByHocKiAndNamBatDauAndNamKetThuc(
                    request.getHocKi(), request.getNamBatDau(), request.getNamKetThuc()
            ).orElseThrow(() -> new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND));

            // Duyệt từng dòng (bỏ dòng header = row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                DataFormatter formatter = new DataFormatter();
                String maSinhVien = formatter.formatCellValue(row.getCell(0)).trim();
                String tenDeTai = formatter.formatCellValue(row.getCell(1)).trim();

                Optional<DeTai> deTaiOpt = deTaiRepository.findByTenDeTaiIgnoreCaseAndSinhVienThucHien_MaSVIgnoreCase(tenDeTai, maSinhVien);

                if (deTaiOpt.isPresent()) {
                    DeTai deTai = deTaiOpt.get();
                    if(deTai.getDotBaoVe() != null) {
                        failures.add(AddSinhVienToDotBaoVeResponse.FailureItem.builder()
                                .maSinhVien(maSinhVien)
                                .tenDeTai(tenDeTai)
                                    .reason("Đề tài đã có trong đợt bảo vệ khác")
                                .build());
                        continue;
                    }
                    if(deTai.getTrangThai() != DeTaiState.ACCEPTED){
                        failures.add(AddSinhVienToDotBaoVeResponse.FailureItem.builder()
                                .maSinhVien(maSinhVien)
                                .tenDeTai(tenDeTai)
                                .reason("Đề tài chưa được duyệt")
                                .build());
                        continue;
                    }
                    deTai.setDotBaoVe(dotBaoVe);
                    deTaiRepository.save(deTai);
                    success++;
                } else {
                    failures.add(AddSinhVienToDotBaoVeResponse.FailureItem.builder()
                            .maSinhVien(maSinhVien)
                            .tenDeTai(tenDeTai)
                            .reason("Không tìm thấy đề tài hoặc sinh viên")
                            .build());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel", e);
        }

        String logFileUrl = null;
        if (!failures.isEmpty()) {
            try {
                File logFile = generateFailureLogExcel(failures);
                logFileUrl = cloudinaryService.upload(logFile);
            } catch (IOException e) {
                throw new RuntimeException("Không thể tạo hoặc upload file log Excel", e);
            }
        }

        return AddSinhVienToDotBaoVeResponse.builder()
                .totalRecords(success + failures.size())
                .successCount(success)
                .failureCount(failures.size())
                .failureItems(failures)
                .logFileUrl(logFileUrl)
                .build();
    }

    private File generateFailureLogExcel(List<AddSinhVienToDotBaoVeResponse.FailureItem> failures) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Failures");

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Sinh Viên");
        headerRow.createCell(1).setCellValue("Tên Đề Tài");
        headerRow.createCell(2).setCellValue("Lý Do");

        // Data
        int rowIdx = 1;
        for (AddSinhVienToDotBaoVeResponse.FailureItem item : failures) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(item.getMaSinhVien());
            row.createCell(1).setCellValue(item.getTenDeTai());
            row.createCell(2).setCellValue(item.getReason());
        }

        // Tạo file tạm
        File tempFile = File.createTempFile("import-failures", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            workbook.write(fos);
        }
        workbook.close();

        return tempFile;
    }
}


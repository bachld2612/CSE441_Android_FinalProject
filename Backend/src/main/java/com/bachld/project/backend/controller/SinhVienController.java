package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienImportResponse;
import com.bachld.project.backend.service.SinhVienService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sinh-vien")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SinhVienController {

    SinhVienService sinhVienService;

    @PostMapping
    public ApiResponse<SinhVienCreationResponse> createSinhVien(@RequestBody SinhVienCreationRequest request) {

        return ApiResponse.<SinhVienCreationResponse>builder()
                .result(sinhVienService.createSinhVien(request))
                .build();

    }

    @PostMapping(value = "import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SinhVienImportResponse>  importSinhVien(@RequestBody MultipartFile file) throws IOException {

        return ApiResponse.<SinhVienImportResponse>builder()
                .result(sinhVienService.importSinhVien(file))
                .build();

    }

}

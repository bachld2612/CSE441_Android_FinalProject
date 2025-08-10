package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.response.taikhoan.AnhDaiDienUploadResponse;
import com.bachld.project.backend.service.TaiKhoanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/tai-khoan")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaiKhoanController {

    TaiKhoanService taiKhoanService;

    @PostMapping(value = "anh-dai-dien", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AnhDaiDienUploadResponse> uploadAnhDaiDien(MultipartFile file) throws IOException {

        return ApiResponse.<AnhDaiDienUploadResponse>builder()
                .result(taiKhoanService.uploadAnhDaiDien(file))
                .build();

    }

}

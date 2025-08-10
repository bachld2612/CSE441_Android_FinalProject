package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;
import com.bachld.project.backend.service.GiangVienService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/giang-vien")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GiangVienController {

    GiangVienService giangVienService;

    @PostMapping
    public ApiResponse<GiangVienCreationResponse> createGiangVien(@RequestBody GiangVienCreationRequest giangVienCreationRequest) {

        return ApiResponse.<GiangVienCreationResponse>builder()
                .result(giangVienService.createGiangVien(giangVienCreationRequest))
                .build();

    }

    @PostMapping("tro-ly-khoa")
    public ApiResponse<String> createTroLyKhoa(@RequestBody TroLyKhoaCreationRequest troLyKhoaCreationRequest) {

        giangVienService.createTroLyKhoa(troLyKhoaCreationRequest);
        return ApiResponse.<String>builder()
                .result("Create Tro Ly Khoa successfully")
                .build();

    }

}

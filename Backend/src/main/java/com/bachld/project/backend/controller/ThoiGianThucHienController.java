package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.thoigianthuchien.ThoiGianThucHienRequest;
import com.bachld.project.backend.dto.response.thoigianthuchien.ThoiGianThucHienResponse;
import com.bachld.project.backend.service.ThoiGianThucHienService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/thoi-gian-thuc-hien")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ThoiGianThucHienController {

    ThoiGianThucHienService thoiGianThucHienService;

    @PostMapping
    public ApiResponse<ThoiGianThucHienResponse> createThoiGianThucHien(@RequestBody ThoiGianThucHienRequest thoiGianThucHienRequest) {

        return ApiResponse.<ThoiGianThucHienResponse>builder()
                .result(thoiGianThucHienService.createThoiGianThucHien(thoiGianThucHienRequest))
                .build();

    }

    @PutMapping("/{thoiGianThucHienId}")
    public ApiResponse<ThoiGianThucHienResponse> updateThoiGianThucHien(
            @RequestBody ThoiGianThucHienRequest thoiGianThucHienRequest,
            @PathVariable Long thoiGianThucHienId) {

        return ApiResponse.<ThoiGianThucHienResponse>builder()
                .result(thoiGianThucHienService.updateThoiGianThucHien(thoiGianThucHienRequest, thoiGianThucHienId))
                .build();

    }

    @GetMapping
    public ApiResponse<Page<ThoiGianThucHienResponse>> getAllThoiGianThucHien(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "dotBaoVe.thoiGianBatDau",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ApiResponse.<Page<ThoiGianThucHienResponse>>builder()
                .result(thoiGianThucHienService.getAllThoiGianThucHien(pageable))
                .build();

    }

}

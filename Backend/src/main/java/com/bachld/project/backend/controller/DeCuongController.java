package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.service.DeCuongService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/de-cuong")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeCuongController {

    DeCuongService deCuongService;

    // Danh sách đề cương:
    // - Giảng viên: chỉ thấy đề cương của SV mình hướng dẫn (service đã lọc theo email GV)
    // - Admin/TBM: xem tất cả
    @GetMapping
    @PreAuthorize("hasAuthority('isAuthenticated()')") // tất cả người dùng đã đăng nhập
    public ApiResponse<Page<DeCuongResponse>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.<Page<DeCuongResponse>>builder()
                .result(deCuongService.getAllDeCuong(pageable))
                .build();
    }

    // Sinh viên nộp/cập nhật đề cương cho Đề tài của chính mình
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    public ApiResponse<DeCuongResponse> submit(@Valid @RequestBody DeCuongRequest request) {
        return ApiResponse.<DeCuongResponse>builder()
                .result(deCuongService.submitDeCuong(request))
                .build();
    }

    // Giảng viên duyệt
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIÊN')") // chỉ GIẢNG_VIÊN được duyệt
    public ApiResponse<DeCuongResponse> approve(@PathVariable("id") Long deCuongId) {
        return ApiResponse.<DeCuongResponse>builder()
                .result(deCuongService.reviewDeCuong(deCuongId, true))
                .build();
    }

    // Giảng viên từ chối
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('SCOPE_GIANG_VIÊN')") // chỉ GIẢNG_VIÊN được duyệt
    public ApiResponse<DeCuongResponse> reject(@PathVariable("id") Long deCuongId) {
        return ApiResponse.<DeCuongResponse>builder()
                .result(deCuongService.reviewDeCuong(deCuongId, false))
                .build();
    }
}

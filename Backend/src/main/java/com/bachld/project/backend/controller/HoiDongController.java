package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.hoidong.HoiDongCreateRequest;
import com.bachld.project.backend.dto.response.hoidong.AddSinhVienToHoiDongResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.enums.HoiDongType;
import com.bachld.project.backend.service.HoiDongService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/hoi-dong")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HoiDongController {

    HoiDongService hoiDongService;

    @GetMapping
    public ApiResponse<Page<HoiDongListItemResponse>> getHoiDongDangDienRa(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) HoiDongType type,
            @PageableDefault(page = 0, size = 10, sort = "thoiGianBatDau", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.<Page<HoiDongListItemResponse>>builder()
                .result(hoiDongService.getHoiDongsDangDienRa(keyword, type, pageable))
                .build();
    }

    @GetMapping("{hoiDongId}")
    public ApiResponse<HoiDongDetailResponse> getHoiDongDetail(@PathVariable Long hoiDongId) {
        return ApiResponse.<HoiDongDetailResponse>builder()
                .result(hoiDongService.getHoiDongDetail(hoiDongId))
                .build();
    }

    @PostMapping("/them-hoi-dong")
    public ApiResponse<HoiDongDetailResponse> createHoiDong(@RequestBody @Valid HoiDongCreateRequest request) {
        return ApiResponse.<HoiDongDetailResponse>builder()
                .result(hoiDongService.createHoiDong(request))
                .build();
    }

    @PostMapping(value = "/{hoiDongId}/import-sinh-vien", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AddSinhVienToHoiDongResponse> importSinhVienToHoiDong(
            @PathVariable Long hoiDongId,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.<AddSinhVienToHoiDongResponse>builder()
                .result(hoiDongService.importSinhVienToHoiDong(hoiDongId, file))
                .build();
    }

    @GetMapping("/hoi-dong-theo-dot")
    public ApiResponse<Page<HoiDongListItemResponse>> getHoiDongTheoDot(
            @RequestParam Long dotBaoVeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) HoiDongType type,
            @PageableDefault(page = 0, size = 10, sort = "thoiGianBatDau", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<Page<HoiDongListItemResponse>>builder()
                .result(hoiDongService.getTatCaHoiDongByDot(dotBaoVeId, keyword, type, pageable))
                .build();
    }
}

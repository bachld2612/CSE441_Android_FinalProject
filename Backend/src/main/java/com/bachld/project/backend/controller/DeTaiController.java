package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.detai.DeTaiGiangVienHuongDanRequest;
import com.bachld.project.backend.dto.request.detai.DeTaiRequest;
import com.bachld.project.backend.dto.request.detai.DeTaiApprovalRequest;
import com.bachld.project.backend.dto.response.detai.DeTaiGiangVienHuongDanResponse;
import com.bachld.project.backend.dto.response.detai.DeTaiResponse;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.service.DeTaiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/de-tai")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeTaiController {

    DeTaiService deTaiService;

    @PostMapping(value = "/dang-ky", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DeTaiResponse> registerDeTai(@ModelAttribute DeTaiRequest request) {
        return ApiResponse.<DeTaiResponse>builder()
                .result(deTaiService.registerDeTai(request))
                .build();
    }

    @GetMapping("/xet-duyet")
    public ApiResponse<Page<DeTaiResponse>> listForGiangVien(
            @RequestParam(defaultValue = "PENDING") DeTaiState trangThai,
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ApiResponse.<Page<DeTaiResponse>>builder()
                .result(deTaiService.getDeTaiByLecturerAndStatus(trangThai, pageable))
                .build();
    }

    @PutMapping("/xet-duyet/{deTaiId}")
    public ApiResponse<DeTaiResponse> approveDeTai(
            @PathVariable Long deTaiId,
            @RequestBody DeTaiApprovalRequest request) {

        return ApiResponse.<DeTaiResponse>builder()
                .result(deTaiService.approveDeTai(deTaiId, request))
                .build();
    }

    @GetMapping("/chi-tiet")
    public ApiResponse<DeTaiResponse> getMyDeTai() {
        return ApiResponse.<DeTaiResponse>builder()
                .result(deTaiService.getMyDeTai())
                .build();
    }

    @PostMapping("/gan-de-tai")
    public ApiResponse<DeTaiGiangVienHuongDanResponse> addGiangVienHuongDan(@RequestBody DeTaiGiangVienHuongDanRequest request) {
        return ApiResponse.<DeTaiGiangVienHuongDanResponse>builder()
                .result(deTaiService.addGiangVienHuongDan(request))
                .build();
    }
}

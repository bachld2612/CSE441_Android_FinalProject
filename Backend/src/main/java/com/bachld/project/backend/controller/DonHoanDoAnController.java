package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.donhoandoan.DonHoanDoAnRequest;
import com.bachld.project.backend.dto.response.donhoandoan.DonHoanDoAnResponse;
import com.bachld.project.backend.service.DonHoanDoAnService;
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
@RequestMapping("/api/v1/don-hoan")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DonHoanDoAnController {

    DonHoanDoAnService donHoanDoAnService;

    // Sinh viên gửi đơn hoãn (lý do + file minh chứng optional)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DonHoanDoAnResponse> createPostponeRequest(
            @ModelAttribute DonHoanDoAnRequest request) {
        return ApiResponse.<DonHoanDoAnResponse>builder()
                .result(donHoanDoAnService.createPostponeRequest(request))
                .build();
    }

    // Sinh viên xem danh sách đơn hoãn của chính mình
    @GetMapping
    public ApiResponse<Page<DonHoanDoAnResponse>> getMyPostponeRequests(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.<Page<DonHoanDoAnResponse>>builder()
                .result(donHoanDoAnService.getMyPostponeRequests(pageable))
                .build();
    }
}
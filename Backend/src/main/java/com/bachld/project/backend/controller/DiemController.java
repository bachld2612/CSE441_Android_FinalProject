package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.diem.DiemCreateRequest;
import com.bachld.project.backend.dto.response.donhoandoan.DonHoanDoAnResponse;
import com.bachld.project.backend.service.DiemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/diem")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DiemController {

    DiemService diemService;

    public ApiResponse<String> chamDiem(@RequestBody DiemCreateRequest request) {
        diemService.chamDiem(request);
        return ApiResponse.<String>builder()
                .result("Chấm điểm thành công")
                .build();
    }
}

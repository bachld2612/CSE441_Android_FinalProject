package com.bachld.project.backend.controller;


import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.nganh.NganhRequest;
import com.bachld.project.backend.dto.response.nganh.NganhResponse;
import com.bachld.project.backend.entity.Nganh;
import com.bachld.project.backend.service.NganhService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nganh")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NganhController {

    NganhService nganhService;

    @GetMapping
    public ApiResponse<Page<NganhResponse>> getAllNganh(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<NganhResponse>>builder()
                .result(nganhService.getAllNganh(pageable))
                .build();
    }

    @PostMapping
    public ApiResponse<NganhResponse> createNganh(@RequestBody @Valid NganhRequest nganhRequest) {
        return ApiResponse.<NganhResponse>builder()
                .result(nganhService.createNganh(nganhRequest))
                .build();
    }

    @PutMapping("/{nganhId}")
    public ApiResponse<NganhResponse> updateNganh(@PathVariable Long nganhId, @RequestBody @Valid NganhRequest nganhRequest) {
        return ApiResponse.<NganhResponse>builder()
                .result(nganhService.updateNganh(nganhRequest, nganhId))
                .build();
    }

    @DeleteMapping("/{nganhId}")
    public ApiResponse<String> deleteNganh(@PathVariable Long nganhId) {
        nganhService.deleteNganh(nganhId);
        return ApiResponse.<String>builder()
                .result("Delete nganh successfully!")
                .build();
    }

}

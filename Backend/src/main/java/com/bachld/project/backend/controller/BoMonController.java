package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.request.bomon.TruongBoMonCreationRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.dto.response.bomon.TruongBoMonCreationResponse;
import com.bachld.project.backend.service.BoMonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bo-mon")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoMonController {

    BoMonService boMonService;

    @GetMapping()
    public ApiResponse<Page<BoMonResponse>> getAllBoMon(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<BoMonResponse>>builder()
                .result(boMonService.getAllBoMon(pageable))
                .build();
    }

    @PostMapping
    public ApiResponse<BoMonResponse> createBoMon(BoMonRequest boMonRequest) {

        return ApiResponse.<BoMonResponse>builder()
                .result(boMonService.createBoMon(boMonRequest))
                .build();

    }

    @PutMapping("{boMonId}")
    public ApiResponse<BoMonResponse> updateBoMon(BoMonRequest boMonRequest, @PathVariable Long boMonId) {

        return ApiResponse.<BoMonResponse>builder()
                .result(boMonService.updateBoMon(boMonRequest, boMonId))
                .build();

    }

    @DeleteMapping
    public ApiResponse<String> deleteBoMon(@RequestParam Long boMonId) {

        boMonService.deleteBoMon(boMonId);
        return ApiResponse.<String>builder()
                .result("Delete bo mon successfully")
                .build();

    }

    @PostMapping("truong-bo-mon")
    public ApiResponse<TruongBoMonCreationResponse> createTruongBoMon(@RequestBody TruongBoMonCreationRequest truongBoMonCreationRequest) {

        return ApiResponse.<TruongBoMonCreationResponse>builder()
                .result(boMonService.createTruongBoMon(truongBoMonCreationRequest))
                .build();

    }

}

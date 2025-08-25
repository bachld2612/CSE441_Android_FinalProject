package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.request.bomon.TruongBoMonCreationRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.dto.response.bomon.BoMonWithTruongBoMonResponse;
import com.bachld.project.backend.dto.response.bomon.TruongBoMonCreationResponse;
import com.bachld.project.backend.service.BoMonService;
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
@RequestMapping(
        value = "/api/v1/bo-mon"
)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoMonController {

    BoMonService boMonService;

    @GetMapping
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

    @PostMapping()
    public ApiResponse<BoMonResponse> createBoMon(@RequestBody @Valid BoMonRequest boMonRequest) {
        return ApiResponse.<BoMonResponse>builder()
                .result(boMonService.createBoMon(boMonRequest))
                .build();
    }

    @PutMapping(value = "{boMonId}")
    public ApiResponse<BoMonResponse> updateBoMon(
            @RequestBody @Valid BoMonRequest boMonRequest,
            @PathVariable Long boMonId) {
        return ApiResponse.<BoMonResponse>builder()
                .result(boMonService.updateBoMon(boMonRequest, boMonId))
                .build();
    }

    @DeleteMapping({ "", "/{boMonId}" })
    public ApiResponse<String> deleteBoMon(
            @RequestParam(value = "boMonId", required = false) Long boMonIdQuery,
            @PathVariable(value = "boMonId", required = false) Long boMonIdPath) {

        Long boMonId = (boMonIdPath != null) ? boMonIdPath : boMonIdQuery;
        if (boMonId == null) {
            throw new IllegalArgumentException("Thiếu tham số 'boMonId'");
        }

        boMonService.deleteBoMon(boMonId);
        return ApiResponse.<String>builder()
                .result("Delete bo mon successfully")
                .build();
    }

    @PostMapping(value = "truong-bo-mon")
    public ApiResponse<TruongBoMonCreationResponse> createTruongBoMon(
            @RequestBody TruongBoMonCreationRequest truongBoMonCreationRequest) {
        return ApiResponse.<TruongBoMonCreationResponse>builder()
                .result(boMonService.createTruongBoMon(truongBoMonCreationRequest))
                .build();
    }

    @GetMapping("/with-truong-bo-mon")
    public ApiResponse<Page<BoMonWithTruongBoMonResponse>> getAllWithTBM(
            @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<Page<BoMonWithTruongBoMonResponse>>builder()
                .result(boMonService.findAllWithTruongBoMon(pageable))
                .build();
    }
}

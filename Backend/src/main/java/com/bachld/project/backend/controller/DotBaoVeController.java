package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import com.bachld.project.backend.service.DotBaoVeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dot-bao-ve")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DotBaoVeController {

    DotBaoVeService dotBaoVeService;

    @PostMapping
    public ApiResponse<DotBaoVeResponse> createDotBaoVe(@RequestBody DotBaoVeRequest dotBaoVeRequest) {

        return ApiResponse.<DotBaoVeResponse>builder()
                .result(dotBaoVeService.createDotBaoVe(dotBaoVeRequest))
                .build();

    }

    @GetMapping
    public ApiResponse<Page<DotBaoVeResponse>> findAllDotBaoVe(
            @PageableDefault(
            page = 0,
            size = 10,
            sort = "updatedAt",
            direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.<Page<DotBaoVeResponse>>builder()
                .result(dotBaoVeService.findAllDotBaoVe(pageable))
                .build();
    }

    @PutMapping("{dotBaoVeId}")
    public ApiResponse<DotBaoVeResponse> updateDotBaoVe(@RequestBody DotBaoVeRequest request, @PathVariable("dotBaoVeId") Long dotBaoVeId) {

        return ApiResponse.<DotBaoVeResponse>builder()
                .result(dotBaoVeService.updateDotBaoVe(request, dotBaoVeId))
                .build();

    }

    @DeleteMapping("{dotBaoVeId}")
    public ApiResponse<String> deleteDotBaoVe(@PathVariable("dotBaoVeId") Long dotBaoVeId) {

        dotBaoVeService.deleteDotBaoVe(dotBaoVeId);
        return ApiResponse.<String>builder()
                .result("Delete dot bao ve successfully")
                .build();

    }

}

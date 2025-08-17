package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.lop.LopRequest;
import com.bachld.project.backend.dto.response.lop.LopResponse;
import com.bachld.project.backend.service.impl.LopService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lop")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LopController {

    LopService lopService;

    @GetMapping
    public ApiResponse<Page<LopResponse>> getLop(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ApiResponse.<Page<LopResponse>>builder()
                .result(lopService.getAllLop(pageable))
                .build();

    }

    @PostMapping
    public ApiResponse<LopResponse> createLop(@RequestBody LopRequest lopRequest) {

        return ApiResponse.<LopResponse>builder()
                .result(lopService.createLop(lopRequest))
                .build();

    }

    @PutMapping("{lopId}")
    public ApiResponse<LopResponse> updateLop(@PathVariable Long lopId, @RequestBody LopRequest lopRequest) {

        return ApiResponse.<LopResponse>builder()
                .result(lopService.updateLop(lopRequest, lopId))
                .build();

    }

    @DeleteMapping("{lopId}")
    public ApiResponse<String> deleteLop(@PathVariable Long lopId) {

        lopService.deleteLop(lopId);
        return ApiResponse.<String>builder()
                .result("Delete lop successfully")
                .build();

    }


}

package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.khoa.KhoaRequest;
import com.bachld.project.backend.dto.response.khoa.KhoaResponse;
import com.bachld.project.backend.service.KhoaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/khoa")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KhoaController {

    KhoaService khoaService;

    @GetMapping
    public ApiResponse<List<KhoaResponse>> getKhoa() {
        return ApiResponse.<List<KhoaResponse>>builder()
                .result(khoaService.getAllKhoa())
                .build();
    }

    @PostMapping
    public ApiResponse<KhoaResponse> createKhoa(@Valid @RequestBody KhoaRequest khoaRequest) {
        return ApiResponse.<KhoaResponse>builder()
                .result(khoaService.createKhoa(khoaRequest))
                .build();
    }

    @PutMapping("/{khoaId}")
    public ApiResponse<KhoaResponse> updateKhoa(@PathVariable Long khoaId,
                                                @Valid @RequestBody KhoaRequest khoaRequest) {
        return ApiResponse.<KhoaResponse>builder()
                .result(khoaService.updateKhoa(khoaRequest, khoaId))
                .build();
    }

    @DeleteMapping("/{khoaId}")  // ⬅️ thêm "/" (trước bạn đang thiếu)
    public ApiResponse<String> deleteKhoa(@PathVariable Long khoaId) {
        khoaService.deleteKhoa(khoaId);
        return ApiResponse.<String>builder()
                .result("Delete khoa successfully")
                .build();
    }
}

package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.decuong.DeCuongLogRequest;
import com.bachld.project.backend.dto.request.decuong.DeCuongUploadRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongLogResponse;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.service.DeCuongService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/de-cuong")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeCuongController {

    DeCuongService deCuongService;

    @GetMapping
    public ApiResponse<Page<DeCuongResponse>> getAll(
            @ParameterObject
            @PageableDefault(page = 0,
                    size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.<Page<DeCuongResponse>>builder()
                .result(deCuongService.getAllDeCuong(pageable))
                .build();
    }

    @PostMapping(value = "/sv/nop-de-cuong", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DeCuongResponse> submitDeCuong(
            @ModelAttribute DeCuongUploadRequest request) throws IOException {
        return ApiResponse.<DeCuongResponse>builder()
                .result(deCuongService.submitDeCuong(request))
                .build();
    }


    @GetMapping("/sv/log")
    public ApiResponse<DeCuongLogResponse> viewDeCuongLog() {
        var res = deCuongService.viewDeCuongLog();
        return ApiResponse.<DeCuongLogResponse>builder().result(res).build();
    }

    @PutMapping("/{id}/duyet")
    public ApiResponse<DeCuongResponse> approveDeCuong(@PathVariable Long id) {
        var res = deCuongService.reviewDeCuong(id, true, null);
        return ApiResponse.<DeCuongResponse>builder().result(res).message("Đã phê duyệt").build();
    }

    @PutMapping(value = "/{id}/tu-choi")
    public ApiResponse<DeCuongResponse> rejectDeCuong(
            @PathVariable Long id,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        if (body != null && (reason == null || reason.isBlank())) {
            reason = toStringVal(body.get("reason"));
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Thiếu tham số 'reason'");
        }
        var res = deCuongService.reviewDeCuong(id, false, reason);
        return ApiResponse.<DeCuongResponse>builder().result(res).message("Đã từ chối").build();
    }

    @GetMapping("/tbm/danh-sach")
    public ApiResponse<Page<DeCuongResponse>> getAcceptedForTBM(
            @PageableDefault(page = 0, size = 10, sort = "deTai.sinhVienThucHien.hoTen", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ApiResponse.<Page<DeCuongResponse>>builder()
                .result(deCuongService.getAcceptedForTBM(pageable))
                .build();
    }

    @GetMapping(
            value = "/tbm/danh-sach/excel",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> exportAcceptedForTBMAsExcel() {
        byte[] xlsx = deCuongService.exportAcceptedForTBMAsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=de-cuong-accepted.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    // ===== Helpers (chỉ trong controller, không ảnh hưởng nơi khác) =====
    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) {
            try {
                return Long.parseLong(s.trim());
            } catch (NumberFormatException ignored) { }
        }
        return null;
    }

    private String toStringVal(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}

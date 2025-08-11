package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/de-cuong")
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

    // Sinh viên nộp/cập nhật đề cương cho Đề tài của chính mình
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_SINH_VIEN')")
    public ApiResponse<DeCuongResponse> submitDeCuong(
            @RequestParam Long deTaiId,
            @RequestParam String fileUrl
    ) {
        var res = deCuongService.submitDeCuong(deTaiId, fileUrl);
        return ApiResponse.<DeCuongResponse>builder().result(res).message("Nộp đề cương thành công").build();
    }


    @PutMapping("/{id}/duyet")
    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN','SCOPE_TRUONG_BO_MON')")
    public ApiResponse<DeCuongResponse> approveDeCuong(@PathVariable Long id) {
        var res = deCuongService.reviewDeCuong(id, true, null);
        return ApiResponse.<DeCuongResponse>builder().result(res).message("Đã phê duyệt").build();
    }

    @PutMapping("/{id}/tu-choi")
    @PreAuthorize("hasAnyAuthority('SCOPE_GIANG_VIEN','SCOPE_TRUONG_BO_MON')")
    public ApiResponse<DeCuongResponse> rejectDeCuong(@PathVariable Long id, @RequestParam String reason) {
        var res = deCuongService.reviewDeCuong(id, false, reason);
        return ApiResponse.<DeCuongResponse>builder().result(res).message("Đã từ chối").build();
    }

    @GetMapping("/tbm/danh-sach")
    public ApiResponse<Page<DeCuongResponse>> getAcceptedForTBM(
//            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "deTai.sinhVienThucHien.hoTen", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ApiResponse.<Page<DeCuongResponse>>builder()
                .result(deCuongService.getAcceptedForTBM(pageable))
                .build();
    }

    @GetMapping(value = "/tbm/danh-sach/export-xlsx",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAcceptedForTBMAsExcel() {
        byte[] xlsx = deCuongService.exportAcceptedForTBMAsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=de-cuong-accepted.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }
}

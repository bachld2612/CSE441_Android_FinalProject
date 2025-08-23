package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.thongbao.ThongBaoCreationRequest;
import com.bachld.project.backend.dto.response.thongbao.ThongBaoCreationResponse;
import com.bachld.project.backend.service.ThongBaoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/thong-bao")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ThongBaoController {

    ThongBaoService thongBaoService;

    @GetMapping
    public ApiResponse<Page<ThongBaoCreationResponse>> getAllThongBao(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ApiResponse.<Page<ThongBaoCreationResponse>>builder()
                .result(thongBaoService.getAllThongBao(pageable))
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ThongBaoCreationResponse> createThongBao(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("tieuDe") String tieuDe,
            @RequestParam("noiDung") String noiDung
    ){
        ThongBaoCreationRequest request = ThongBaoCreationRequest.builder()
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .file(file)
                .build();
        return ApiResponse.<ThongBaoCreationResponse>builder()
                .result(thongBaoService.createThongBao(request))
                .build();
    }

    @GetMapping("/{thongBaoId}")
    public ApiResponse<ThongBaoCreationResponse> getThongBaoById(@PathVariable Long thongBaoId){
        return ApiResponse.<ThongBaoCreationResponse>builder()
                .result(thongBaoService.getThongBaoById(thongBaoId))
                .build();
    }

}

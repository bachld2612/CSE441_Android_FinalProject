package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.*;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.service.GiangVienService;
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

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/giang-vien")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GiangVienController {

    GiangVienService giangVienService;

    @PostMapping
    public ApiResponse<GiangVienCreationResponse> createGiangVien(@RequestBody GiangVienCreationRequest giangVienCreationRequest) {

        return ApiResponse.<GiangVienCreationResponse>builder()
                .result(giangVienService.createGiangVien(giangVienCreationRequest))
                .build();

    }

    @PostMapping("tro-ly-khoa")
    public ApiResponse<String> createTroLyKhoa(@RequestBody TroLyKhoaCreationRequest troLyKhoaCreationRequest) {

        giangVienService.createTroLyKhoa(troLyKhoaCreationRequest);
        return ApiResponse.<String>builder()
                .result("Create Tro Ly Khoa successfully")
                .build();

    }

    @PostMapping(value = "import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<GiangVienImportResponse> importGiangVien(@RequestBody MultipartFile file) throws IOException {

        return ApiResponse.<GiangVienImportResponse>builder()
                .result(giangVienService.importGiangVien(file))
                .build();

    }

    @GetMapping("/sinh-vien")
    public ApiResponse<Page<SinhVienSupervisedResponse>> getMySupervisedStudents(
            @PageableDefault(page = 0, size = 10, sort = "hoTen", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return ApiResponse.<Page<SinhVienSupervisedResponse>>builder()
                .result(giangVienService.getMySupervisedStudents(pageable))
                .build();
    }

    @GetMapping("/xet-duyet/sinh-vien")
    public ApiResponse<Page<DeTaiSinhVienApprovalResponse>> getDeTaiSinhVienApproval(
            @RequestParam(name = "status", defaultValue = "ACCEPTED") DeTaiState status,
            @PageableDefault(page = 0, size = 10, sort = "hoTen", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return ApiResponse.<Page<DeTaiSinhVienApprovalResponse>>builder()
                .result(giangVienService.getDeTaiSinhVienApproval(status, pageable))
                .build();
    }

    @GetMapping("/{boMonId}")
    public ApiResponse<Set<GiangVienInfoResponse>> getGiangVienByBoMon(@PathVariable("boMonId") Long boMonId) {

        return ApiResponse.<Set<GiangVienInfoResponse>>builder()
                .result(giangVienService.getGiangVienByBoMon(boMonId))
                .build();

    }

}

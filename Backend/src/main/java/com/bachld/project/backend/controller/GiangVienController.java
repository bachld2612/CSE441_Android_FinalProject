package com.bachld.project.backend.controller;

import com.bachld.project.backend.dto.ApiResponse;
import com.bachld.project.backend.dto.request.detai.DeTaiApprovalRequest;
import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.GiangVienUpdateRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.*;
import com.bachld.project.backend.dto.response.detai.DeTaiResponse;
import com.bachld.project.backend.dto.response.giangvien.DeTaiSinhVienApprovalResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienImportResponse;
import com.bachld.project.backend.dto.response.giangvien.SinhVienSupervisedResponse;
import com.bachld.project.backend.enums.DeTaiState;
import com.bachld.project.backend.service.GiangVienService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import com.bachld.project.backend.service.DeTaiService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/giang-vien")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GiangVienController {

    GiangVienService giangVienService;
    DeTaiService deTaiService;

    @PostMapping
    public ApiResponse<GiangVienCreationResponse> createGiangVien(@RequestBody @Valid GiangVienCreationRequest giangVienCreationRequest) {

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
                .result(giangVienService.getMySinhVienSupervised(pageable))
                .build();
    }

    @GetMapping("/do-an/xet-duyet-de-tai")
    public ApiResponse<Page<DeTaiSinhVienApprovalResponse>> getDeTaiSinhVienApproval(
            @RequestParam(name = "status", required = false) DeTaiState status,
            @PageableDefault(page = 0, size = 10, sort = "hoTen", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return ApiResponse.<Page<DeTaiSinhVienApprovalResponse>>builder()
                .result(giangVienService.getDeTaiSinhVienApproval(status, pageable))
                .build();
    }

    @GetMapping("/{boMonId}")
    public ApiResponse<Set<GiangVienInfoResponse>> getGiangVienByBoMon(@PathVariable("boMonId") Long boMonId) {

        return ApiResponse.<Set<GiangVienInfoResponse>>builder()
                .result(giangVienService.getGiangVienByBoMonAndSoLuongDeTai(boMonId))
                .build();

    }

    @GetMapping("/by-bo-mon/{boMonId}")
    public ApiResponse<List<GiangVienLiteResponse>> getByBoMon(@PathVariable Long boMonId) {
        return ApiResponse.<List<GiangVienLiteResponse>>builder()
                .result(giangVienService.getGiangVienLiteByBoMon(boMonId))
                .build();
    }

    @GetMapping("/list")
    public ApiResponse<Page<GiangVienResponse>> listGiangVien(
            @PageableDefault(size = 10, sort = "maGV", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ApiResponse.<Page<GiangVienResponse>>builder()
                .result(giangVienService.getAllGiangVien(pageable))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<GiangVienResponse> updateGiangVien(
            @PathVariable Long id,
            @RequestBody @Valid GiangVienUpdateRequest request) {

        return ApiResponse.<GiangVienResponse>builder()
                .result(giangVienService.updateGiangVien(id, request))
                .build();
    }

    @PutMapping("/do-an/xet-duyet-de-tai/{deTaiId}/approve")
    public ApiResponse<DeTaiResponse> approveDeTaiByLecturer(
            @PathVariable Long deTaiId,
            @RequestBody(required = false) DeTaiApprovalRequest request) {

        if (request == null) request = new DeTaiApprovalRequest();
        request.setApproved(true);

        return ApiResponse.<DeTaiResponse>builder()
                .result(deTaiService.approveByGiangVien(deTaiId, request.getNhanXet()))
                .build();
    }

    @PutMapping("/do-an/xet-duyet-de-tai/{deTaiId}/reject")
    @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = false,
            content = @Content(examples = @ExampleObject(
                    name = "Reject body", value = "{ \"approved\": false, \"nhanXet\": \"Lý do từ chối\" }"
            ))
    ))
    public ApiResponse<DeTaiResponse> rejectDeTaiByLecturer(
            @PathVariable Long deTaiId,
            @RequestBody(required = false) @Valid DeTaiApprovalRequest request) {
        if (request == null) request = new DeTaiApprovalRequest();
        request.setApproved(false); // ép false bất kể client gửi gì
        return ApiResponse.<DeTaiResponse>builder()
                .result(deTaiService.rejectByGiangVien(deTaiId, request.getNhanXet()))
                .build();
    }

    @GetMapping("/sinh-vien/all")
    public ApiResponse<List<StudentSupervisedResponse>> getMySupervisedStudentsAll(
            @RequestParam(required = false) String q) {

        return ApiResponse.<List<StudentSupervisedResponse>>builder()
                .result(giangVienService.getMySinhVienSupervisedAll(q))
                .build();
    }


}
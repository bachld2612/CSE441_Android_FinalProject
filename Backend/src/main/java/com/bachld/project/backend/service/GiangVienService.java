package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.GiangVienUpdateRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.*;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.List;

public interface GiangVienService {
    GiangVienCreationResponse createGiangVien(GiangVienCreationRequest giangVienCreationRequest);
    void createTroLyKhoa(TroLyKhoaCreationRequest troLyKhoaCreationRequest);
    GiangVienImportResponse importGiangVien(MultipartFile file) throws IOException;
    Page<SinhVienSupervisedResponse> getMySinhVienSupervised(Pageable pageable);
    Page<DeTaiSinhVienApprovalResponse> getDeTaiSinhVienApproval(DeTaiState status, Pageable pageable);
    List<GiangVienLiteResponse> getGiangVienLiteByBoMon(Long boMonId);
    Page<GiangVienResponse> getAllGiangVien(Pageable pageable);
    GiangVienResponse updateGiangVien(Long id, GiangVienUpdateRequest request);
    Set<GiangVienInfoResponse> getGiangVienByBoMonAndSoLuongDeTai(Long boMonId);
    List<StudentSupervisedResponse> getMySinhVienSupervisedAll(String q);
}

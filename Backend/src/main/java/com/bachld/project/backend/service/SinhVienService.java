package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.request.sinhvien.SinhVienUpdateRequest;
import com.bachld.project.backend.dto.response.sinhvien.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SinhVienService {

    SinhVienCreationResponse createSinhVien(SinhVienCreationRequest request);
    SinhVienImportResponse importSinhVien(MultipartFile file) throws IOException;
    Page<SinhVienResponse> getAllSinhVien(Pageable pageable);
    Page<SinhVienResponse> getAllSinhVienByTenOrMaSV(String request, Pageable pageable);
    void changeSinhVienStatus(String maSV);
    SinhVienCreationResponse updateSinhVien(SinhVienUpdateRequest request, String maSV);
    SinhVienInfoResponse getSinhVienInfo(String maSV);
    List<GetSinhVienWithoutDeTaiResponse> getSinhVienWithoutDeTai();
    void uploadCV(MultipartFile file) throws IOException;
}

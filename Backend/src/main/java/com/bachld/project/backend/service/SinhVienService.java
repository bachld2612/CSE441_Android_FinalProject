package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SinhVienService {

    SinhVienCreationResponse createSinhVien(SinhVienCreationRequest request);
    SinhVienImportResponse importSinhVien(MultipartFile file) throws IOException;

}

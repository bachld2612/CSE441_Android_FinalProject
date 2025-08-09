package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;

public interface SinhVienService {

    SinhVienCreationResponse createSinhVien(SinhVienCreationRequest request);

}

package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GiangVienService {

    GiangVienCreationResponse createGiangVien(GiangVienCreationRequest giangVienCreationRequest);
    void createTroLyKhoa(TroLyKhoaCreationRequest troLyKhoaCreationRequest);
    GiangVienImportResponse importGiangVien(MultipartFile file) throws IOException;

}

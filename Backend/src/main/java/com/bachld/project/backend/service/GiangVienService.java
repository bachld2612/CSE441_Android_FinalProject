package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.request.giangvien.TroLyKhoaCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;

public interface GiangVienService {

    GiangVienCreationResponse createGiangVien(GiangVienCreationRequest giangVienCreationRequest);
    void createTroLyKhoa(TroLyKhoaCreationRequest troLyKhoaCreationRequest);

}

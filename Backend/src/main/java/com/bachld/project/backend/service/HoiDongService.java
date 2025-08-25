package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.hoidong.HoiDongCreateRequest;
import com.bachld.project.backend.dto.response.hoidong.AddSinhVienToHoiDongResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.enums.HoiDongType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface HoiDongService {
    Page<HoiDongListItemResponse> getHoiDongsDangDienRa(String keyword, HoiDongType type, Pageable pageable);
    HoiDongDetailResponse getHoiDongDetail(Long hoiDongId);
    HoiDongDetailResponse createHoiDong(HoiDongCreateRequest request);
    AddSinhVienToHoiDongResponse importSinhVienToHoiDong(Long hoiDongId, MultipartFile excelFile);
    Page<HoiDongListItemResponse> getTatCaHoiDongByDot(Long dotBaoVeId, String keyword, HoiDongType type, Pageable pageable);
}
package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.enums.HoiDongType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HoiDongService {
    Page<HoiDongListItemResponse> getHoiDongsDangDienRa(String keyword, HoiDongType type, Pageable pageable);
    HoiDongDetailResponse getHoiDongDetail(Long hoiDongId);
}
package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.thoigianthuchien.ThoiGianThucHienRequest;
import com.bachld.project.backend.dto.response.thoigianthuchien.ThoiGianThucHienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThoiGianThucHienService {

    ThoiGianThucHienResponse createThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest);
    ThoiGianThucHienResponse updateThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest, Long thoiGianThucHienId);

    Page<ThoiGianThucHienResponse> getAllThoiGianThucHien(Pageable pageable);
}

package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeCuongService {
    // DeCuongService.java
    DeCuongResponse submitDeCuong(DeCuongRequest request);
    DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve, String reason);
    Page<DeCuongResponse> getAllDeCuong(Pageable pageable);

}

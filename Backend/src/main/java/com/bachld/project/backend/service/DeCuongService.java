package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.decuong.DeCuongUploadRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongLogResponse;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeCuongService {
    // DeCuongService.java
    DeCuongResponse submitDeCuong(DeCuongUploadRequest request);
    DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve, String reason);
    Page<DeCuongResponse> getAllDeCuong(Pageable pageable);
    /** TBM xem danh sách đề cương đã duyệt của bộ môn quản lý */
    Page<DeCuongResponse> getAcceptedForTBM(Pageable pageable);
    byte[] exportAcceptedForTBMAsExcel();
    DeCuongLogResponse viewDeCuongLog();
}

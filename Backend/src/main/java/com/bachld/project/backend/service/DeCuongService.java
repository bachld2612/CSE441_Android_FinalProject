package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeCuongService {

    /** Sinh viên nộp đề cương (tạo mới hoặc cập nhật file, status về PENDING) */
    DeCuongResponse submitDeCuong(DeCuongRequest request);

    /** Giảng viên/Quản trị xét duyệt đề cương */
    DeCuongResponse reviewDeCuong(Long deCuongId, boolean approve);

    Page<DeCuongResponse> getAllDeCuong(Pageable pageable);
}

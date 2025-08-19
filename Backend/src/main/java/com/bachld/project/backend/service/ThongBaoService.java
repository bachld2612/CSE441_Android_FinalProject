package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.thongbao.ThongBaoCreationRequest;
import com.bachld.project.backend.dto.response.thongbao.ThongBaoCreationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThongBaoService {

    ThongBaoCreationResponse createThongBao(ThongBaoCreationRequest thongBaoCreationRequest);
    Page<ThongBaoCreationResponse> getAllThongBao(Pageable pageable);
    ThongBaoCreationResponse getThongBaoById(Long id);

}

package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.donhoandoan.DonHoanDoAnRequest;
import com.bachld.project.backend.dto.response.donhoandoan.DonHoanDoAnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonHoanDoAnService {
    DonHoanDoAnResponse createPostponeRequest(DonHoanDoAnRequest request);
    Page<DonHoanDoAnResponse> getMyPostponeRequests(Pageable pageable);
}

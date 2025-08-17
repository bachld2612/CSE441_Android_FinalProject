package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.detai.DeTaiRequest;
import com.bachld.project.backend.dto.request.detai.DeTaiApprovalRequest;
import com.bachld.project.backend.dto.response.detai.DeTaiResponse;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeTaiService {
    DeTaiResponse registerDeTai(DeTaiRequest request);

    Page<DeTaiResponse> getDeTaiByLecturerAndStatus(DeTaiState trangThai, Pageable pageable);

    DeTaiResponse approveDeTai(Long deTaiId, DeTaiApprovalRequest request);

    DeTaiResponse getMyDeTai();
}
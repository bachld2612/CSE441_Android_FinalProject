package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.lop.LopRequest;
import com.bachld.project.backend.dto.response.lop.LopResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LopService {

    LopResponse createLop(LopRequest lopRequest);
    LopResponse updateLop(LopRequest lopRequest, Long lopId);
    void deleteLop(Long lopId);
    Page<LopResponse> getAllLop(Pageable pageable);
}

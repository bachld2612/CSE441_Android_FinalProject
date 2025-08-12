package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DotBaoVeService {

    DotBaoVeResponse createDotBaoVe(DotBaoVeRequest request);
    DotBaoVeResponse updateDotBaoVe(DotBaoVeRequest request, Long dotBaoVeId);
    void deleteDotBaoVe(Long id);
    Page<DotBaoVeResponse> findAllDotBaoVe(Pageable pageable);

}

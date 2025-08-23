package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.dotbaove.AddSinhVienToDotBaoVeRequest;
import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.AddSinhVienToDotBaoVeResponse;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface DotBaoVeService {

    DotBaoVeResponse createDotBaoVe(DotBaoVeRequest request);
    DotBaoVeResponse updateDotBaoVe(DotBaoVeRequest request, Long dotBaoVeId);
    void deleteDotBaoVe(Long id);
    Page<DotBaoVeResponse> findAllDotBaoVe(Pageable pageable);
    AddSinhVienToDotBaoVeResponse addSinhVienToDotBaoVe(AddSinhVienToDotBaoVeRequest request) throws IOException;

}

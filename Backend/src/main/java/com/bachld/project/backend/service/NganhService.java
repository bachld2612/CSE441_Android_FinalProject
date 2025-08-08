package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.nganh.NganhRequest;
import com.bachld.project.backend.dto.response.nganh.NganhResponse;

import java.util.List;

public interface NganhService {

    NganhResponse createNganh(NganhRequest nganhRequest);
    NganhResponse updateNganh(NganhRequest nganhRequest, Long nganhId);
    void deleteNganh(Long nganhId);
    List<NganhResponse> getAllNganh();

}

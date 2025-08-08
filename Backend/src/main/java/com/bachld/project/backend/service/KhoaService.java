package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.khoa.KhoaRequest;
import com.bachld.project.backend.dto.response.khoa.KhoaResponse;

import java.util.List;

public interface KhoaService {

    KhoaResponse createKhoa(KhoaRequest khoaRequest);
    KhoaResponse updateKhoa(KhoaRequest khoaRequest, Long khoaId);
    void deleteKhoa(Long khoaId);
    List<KhoaResponse> getAllKhoa();

}

package com.bachld.project.backend.service;

import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.request.bomon.TruongBoMonCreationRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.dto.response.bomon.TruongBoMonCreationResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienLite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoMonService {

    BoMonResponse createBoMon(BoMonRequest boMonRequest);
    BoMonResponse updateBoMon(BoMonRequest boMonRequest, Long boMonId);
    void deleteBoMon(Long boMonId);
    Page<BoMonResponse> getAllBoMon(Pageable pageable);
    TruongBoMonCreationResponse createTruongBoMon(TruongBoMonCreationRequest truongBoMonCreationRequest);
    List<GiangVienLite> getGiangVienByBoMon(Long boMonId);
}

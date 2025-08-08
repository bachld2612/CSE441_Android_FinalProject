package com.bachld.project.backend.service.impl;


import com.bachld.project.backend.dto.request.lop.LopRequest;
import com.bachld.project.backend.dto.response.lop.LopResponse;
import com.bachld.project.backend.entity.Lop;
import com.bachld.project.backend.entity.Nganh;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.LopMapper;
import com.bachld.project.backend.repository.LopRepository;
import com.bachld.project.backend.repository.NganhRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class LopService implements com.bachld.project.backend.service.LopService {

    LopRepository lopRepository;
    LopMapper lopMapper;
    NganhRepository nganhRepository;



    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public LopResponse createLop(LopRequest lopRequest) {
        if(lopRepository.existsByTenLopIgnoreCase(lopRequest.getTenLop())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_LOP);
        }
        return lopMapper.toLopResponse(lopRepository.save(lopMapper.toLop(lopRequest)));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public LopResponse updateLop(LopRequest lopRequest, Long lopId) {
        if(lopRepository.existsByTenLopIgnoreCase(lopRequest.getTenLop())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_LOP);
        }
        Lop lop = lopRepository.findById(lopId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.LOP_NOT_FOUND));
        lop.setTenLop(lopRequest.getTenLop());
        Nganh nganh = nganhRepository.findById(lopRequest.getNganhId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.NGANH_NOT_FOUND));
        lop.setNganh(nganh);
        return lopMapper.toLopResponse(lopRepository.save(lopMapper.toLop(lopRequest)));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public void deleteLop(Long lopId) {
        lopRepository.deleteById(lopId);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<LopResponse> getAllLop(Pageable pageable) {
        Page<Lop> lopPage = lopRepository.findAll(pageable);
        return lopPage.map(lopMapper::toLopResponse);
    }
}

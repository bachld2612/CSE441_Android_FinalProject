package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.nganh.NganhRequest;
import com.bachld.project.backend.dto.response.nganh.NganhResponse;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.entity.Nganh;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.NganhMapper;
import com.bachld.project.backend.repository.KhoaRepository;
import com.bachld.project.backend.repository.NganhRepository;
import com.bachld.project.backend.service.NganhService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class NganhServiceImpl implements NganhService {

    NganhRepository nganhRepository;
    NganhMapper nganhMapper;
    KhoaRepository khoaRepository;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public NganhResponse createNganh(NganhRequest nganhRequest) {
        if(nganhRepository.existsByTenNganhIgnoreCase(nganhRequest.getTenNganh())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_NGANH);
        }
        return nganhMapper.toNganhResponse(nganhRepository.save(nganhMapper.toNganh(nganhRequest)));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public NganhResponse updateNganh(NganhRequest nganhRequest, Long nganhId) {
        if(nganhRepository.existsByTenNganhIgnoreCase(nganhRequest.getTenNganh())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_NGANH);
        }
        Nganh nganh = nganhRepository.findById(nganhId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NGANH_NOT_FOUND));
        nganh.setTenNganh(nganhRequest.getTenNganh());
        Khoa khoa = khoaRepository.findById(nganhRequest.getKhoaId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.KHOA_NOT_FOUND));
        nganh.setKhoa(khoa);
        return nganhMapper.toNganhResponse(nganhRepository.save(nganh));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public void deleteNganh(Long nganhId) {
        nganhRepository.deleteById(nganhId);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<NganhResponse> getAllNganh(Pageable pageable) {
        Page<Nganh> nganhPage = nganhRepository.findAll(pageable);
        return nganhPage.map(nganhMapper::toNganhResponse);
    }
}

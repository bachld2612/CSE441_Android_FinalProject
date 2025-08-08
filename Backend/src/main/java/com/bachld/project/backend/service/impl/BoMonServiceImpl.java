package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.BoMonMapper;
import com.bachld.project.backend.repository.BoMonRepository;
import com.bachld.project.backend.repository.KhoaRepository;
import com.bachld.project.backend.service.BoMonService;
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
public class BoMonServiceImpl implements BoMonService {

    BoMonRepository boMonRepository;
    BoMonMapper boMonMapper;
    KhoaRepository khoaRepository;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public BoMonResponse createBoMon(BoMonRequest boMonRequest) {
        if(boMonRepository.existsByTenBoMonIgnoreCase(boMonRequest.getTenBoMon())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_BO_MON);
        }
        return boMonMapper.toBoMonResponse(boMonRepository.save(boMonMapper.toBoMon(boMonRequest)));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public BoMonResponse updateBoMon(BoMonRequest boMonRequest, Long boMonId) {
        if(boMonRepository.existsByTenBoMonIgnoreCase(boMonRequest.getTenBoMon())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_BO_MON);
        }
        BoMon boMon = boMonRepository.findById(boMonId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));
        boMon.setTenBoMon(boMonRequest.getTenBoMon());
        Khoa khoa = khoaRepository.findById(boMonRequest.getKhoaId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.KHOA_NOT_FOUND));
        boMon.setKhoa(khoa);
        return boMonMapper.toBoMonResponse(boMonRepository.save(boMon));
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public void deleteBoMon(Long boMonId) {
        boMonRepository.deleteById(boMonId);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<BoMonResponse> getAllBoMon(Pageable pageable) {
        Page<BoMon> boMonPage = boMonRepository.findAll(pageable);
        return boMonPage.map(boMonMapper::toBoMonResponse);
    }
}

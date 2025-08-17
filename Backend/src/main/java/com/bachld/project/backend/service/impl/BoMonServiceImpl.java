package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.request.bomon.TruongBoMonCreationRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.dto.response.bomon.TruongBoMonCreationResponse;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.BoMonMapper;
import com.bachld.project.backend.repository.BoMonRepository;
import com.bachld.project.backend.repository.GiangVienRepository;
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
    private final GiangVienRepository giangVienRepository;

    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
    @Override
    public BoMonResponse createBoMon(BoMonRequest boMonRequest) {
        if(boMonRepository.existsByTenBoMonIgnoreCase(boMonRequest.getTenBoMon())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_BO_MON);
        }
        return boMonMapper.toBoMonResponse(boMonRepository.save(boMonMapper.toBoMon(boMonRequest)));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
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


    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
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

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public TruongBoMonCreationResponse createTruongBoMon(TruongBoMonCreationRequest truongBoMonCreationRequest) {

        GiangVien truongBoMon = giangVienRepository.findById(truongBoMonCreationRequest.getGiangVienId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.GIANG_VIEN_NOT_FOUND));
        BoMon boMon = boMonRepository.findById(truongBoMonCreationRequest.getBoMonId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));
        if(truongBoMon.getBoMonQuanLy() != null) {
            throw new ApplicationException(ErrorCode.TRUONG_BO_MON_ALREADY);
        }
        if(truongBoMon.getBoMon() != boMon) {
            throw new ApplicationException(ErrorCode.NOT_IN_BO_MON);
        }
        if(boMon.getTruongBoMon() != null) {
            GiangVien currentTruongBoMon = boMon.getTruongBoMon();
            currentTruongBoMon.getTaiKhoan().setVaiTro(Role.GIANG_VIEN);
            currentTruongBoMon.setBoMonQuanLy(null);
            giangVienRepository.save(currentTruongBoMon);
        }
        truongBoMon.setBoMonQuanLy(boMon);
        truongBoMon.getTaiKhoan().setVaiTro(Role.TRUONG_BO_MON);
        boMon.setTruongBoMon(truongBoMon);
        giangVienRepository.save(truongBoMon);
        return boMonMapper.toTruongBoMonCreationResponse(boMonRepository.save(boMon));

    }
}

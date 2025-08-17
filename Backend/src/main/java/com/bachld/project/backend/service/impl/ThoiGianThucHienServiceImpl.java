package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.thoigianthuchien.ThoiGianThucHienRequest;
import com.bachld.project.backend.dto.response.thoigianthuchien.ThoiGianThucHienResponse;
import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.ThoiGianThucHienMapper;
import com.bachld.project.backend.repository.DotBaoVeRepository;
import com.bachld.project.backend.repository.ThoiGianThucHienRepository;
import com.bachld.project.backend.service.ThoiGianThucHienService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class ThoiGianThucHienServiceImpl implements ThoiGianThucHienService {

    ThoiGianThucHienRepository thoiGianThucHienRepository;
    DotBaoVeRepository dotBaoVeRepository;
    private final ThoiGianThucHienMapper thoiGianThucHienMapper;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public ThoiGianThucHienResponse createThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest) {
        DotBaoVe dotBaoVe = validateThoiGianThucHien(thoiGianThucHienRequest);
        var entity = thoiGianThucHienMapper.toThoiGianThucHien(thoiGianThucHienRequest);
        entity.setDotBaoVe(dotBaoVe);
        entity = thoiGianThucHienRepository.save(entity);

        return thoiGianThucHienMapper.toThoiGianThucHienResponse(entity);
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public ThoiGianThucHienResponse updateThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest, Long thoiGianThucHienId) {
        var thoiGianThucHien = thoiGianThucHienRepository.findById(thoiGianThucHienId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.THOI_GIAN_THUC_HIEN_NOT_FOUND));
        DotBaoVe dotBaoVe = validateThoiGianThucHien(thoiGianThucHienRequest);
        thoiGianThucHienMapper.updateThoiGianThucHienFromDto(thoiGianThucHienRequest, thoiGianThucHien);
        return thoiGianThucHienMapper.toThoiGianThucHienResponse(thoiGianThucHienRepository.save(thoiGianThucHien));
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<ThoiGianThucHienResponse> getAllThoiGianThucHien(Pageable pageable) {
        Page<ThoiGianThucHien> thoiGianThucHienPage = thoiGianThucHienRepository.findAll(pageable);
        return thoiGianThucHienPage.map(thoiGianThucHienMapper::toThoiGianThucHienResponse);
    }

    private DotBaoVe validateThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest) {

        DotBaoVe dotBaoVe = dotBaoVeRepository.findById(thoiGianThucHienRequest.getDotBaoVeId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND));
        if(thoiGianThucHienRequest.getThoiGianBatDau().isAfter(thoiGianThucHienRequest.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
        if (thoiGianThucHienRequest.getThoiGianBatDau().isBefore(dotBaoVe.getThoiGianBatDau()) ||
                thoiGianThucHienRequest.getThoiGianKetThuc().isAfter(dotBaoVe.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.INVALID_TIME_RANGE);
        }
        if (thoiGianThucHienRepository.existsByDotBaoVeAndCongViec(
                dotBaoVe, thoiGianThucHienRequest.getCongViec())) {
            throw new ApplicationException(ErrorCode.CONG_VIEC_EXISTED);
        }
        return dotBaoVe;
    }
}

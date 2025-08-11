package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.DotBaoVeMapper;
import com.bachld.project.backend.repository.DotBaoVeRepository;
import com.bachld.project.backend.service.DotBaoVeService;
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
public class DotBaoVeServiceImpl implements DotBaoVeService {

    DotBaoVeRepository dotBaoVeRepository;
    DotBaoVeMapper dotBaoVeMapper;

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public DotBaoVeResponse createDotBaoVe(DotBaoVeRequest request) {

        validateDotBaoVeTime(request);
        return dotBaoVeMapper.toDotBaoVeResponse(dotBaoVeRepository.save(dotBaoVeMapper.toDotBaoVe(request)));

    }
    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public DotBaoVeResponse updateDotBaoVe(DotBaoVeRequest request, Long dotBaoVeId) {
        DotBaoVe dotBaoVe = dotBaoVeRepository.findById(dotBaoVeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DOT_BAO_VE_NOT_FOUND));
        if(dotBaoVeRepository.existsByTenDotBaoVeAndIdNot(request.getTenDotBaoVe(), dotBaoVeId)){
            throw new ApplicationException(ErrorCode.DUPLICATED_DOT_BAO_VE);
        }
        if (request.getThoiGianBatDau().isAfter(request.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.DOT_BAO_VE_INVALID);
        }
        if(request.getThoiGianBatDau().getYear() != request.getNamBatDau()
                && request.getThoiGianBatDau().getYear() != request.getNamKetThuc()){
            throw new ApplicationException(ErrorCode.DOT_BAO_VE_INVALID);
        }
        dotBaoVeMapper.updateDotBaoVeFromDto(request, dotBaoVe);
        return dotBaoVeMapper.toDotBaoVeResponse(dotBaoVeRepository.save(dotBaoVe));
    }

    private void validateDotBaoVeTime(DotBaoVeRequest request) {
        if(dotBaoVeRepository.existsByTenDotBaoVe(request.getTenDotBaoVe())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_DOT_BAO_VE);
        }
        if (request.getThoiGianBatDau().isAfter(request.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.DOT_BAO_VE_INVALID);
        }
        if(request.getThoiGianBatDau().getYear() != request.getNamBatDau()
                && request.getThoiGianBatDau().getYear() != request.getNamKetThuc()){
            throw new ApplicationException(ErrorCode.DOT_BAO_VE_INVALID);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public void deleteDotBaoVe(Long id) {

        dotBaoVeRepository.deleteById(id);

    }

    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public Page<DotBaoVeResponse> findAllDotBaoVe(Pageable pageable) {
        Page<DotBaoVe> page = dotBaoVeRepository.findAll(pageable);
        return page.map(dotBaoVeMapper::toDotBaoVeResponse);
    }

}

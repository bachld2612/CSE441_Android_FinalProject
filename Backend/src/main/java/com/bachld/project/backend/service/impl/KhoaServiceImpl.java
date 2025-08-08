package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.khoa.KhoaRequest;
import com.bachld.project.backend.dto.response.khoa.KhoaResponse;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.KhoaMapper;
import com.bachld.project.backend.repository.KhoaRepository;
import com.bachld.project.backend.service.KhoaService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class KhoaServiceImpl implements KhoaService {

    KhoaRepository khoaRepository;
    private final KhoaMapper khoaMapper;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Override
    public KhoaResponse createKhoa(KhoaRequest khoaRequest) {

        if (khoaRepository.existsByTenKhoaIgnoreCase(khoaRequest.getTenKhoa())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_KHOA);
        }
        return khoaMapper.toKhoaResponse(khoaRepository.save(khoaMapper.toKhoa(khoaRequest)));

    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Override
    public KhoaResponse updateKhoa(KhoaRequest khoaRequest, Long khoaId) {
        if (khoaRepository.existsByTenKhoaIgnoreCase(khoaRequest.getTenKhoa())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_KHOA);
        }
        Khoa khoa = khoaRepository.findById(khoaId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.KHOA_NOT_FOUND));
        khoa.setTenKhoa(khoaRequest.getTenKhoa());
        return khoaMapper.toKhoaResponse(khoaRepository.save(khoa));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Override
    public void deleteKhoa(Long khoaId) {
        khoaRepository.deleteById(khoaId);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<KhoaResponse> getAllKhoa() {
        return khoaRepository.findAll().stream().map(khoaMapper::toKhoaResponse).collect(Collectors.toList());
    }
}

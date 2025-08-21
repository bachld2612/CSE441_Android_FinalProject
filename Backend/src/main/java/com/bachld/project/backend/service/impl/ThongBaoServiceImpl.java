package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.thongbao.ThongBaoCreationRequest;
import com.bachld.project.backend.dto.response.thongbao.ThongBaoCreationResponse;
import com.bachld.project.backend.entity.ThongBao;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.ThongBaoMapper;
import com.bachld.project.backend.repository.ThongBaoRepository;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.ThongBaoService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class ThongBaoServiceImpl implements ThongBaoService {

    ThongBaoRepository thongBaoRepository;
    ThongBaoMapper  thongBaoMapper;
    CloudinaryService cloudinaryService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_TRO_LY_KHOA')")
    @Override
    public ThongBaoCreationResponse createThongBao(ThongBaoCreationRequest thongBaoCreationRequest) {
        String fileUrl = null;
        if(thongBaoCreationRequest.getFile() != null)
        {
            fileUrl = cloudinaryService.uploadRawFile(thongBaoCreationRequest.getFile());
        }
        ThongBao thongBao = thongBaoMapper.toThongBao(thongBaoCreationRequest);
        thongBao.setFileUrl(fileUrl);
        ThongBao result = thongBaoRepository.save(thongBao);
        return thongBaoMapper.toThongBaoCreationResponse(result);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<ThongBaoCreationResponse> getAllThongBao(Pageable pageable) {
        Page<ThongBao> thongBao = thongBaoRepository.findAll(pageable);
        return thongBao.map(thongBaoMapper::toThongBaoCreationResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public ThongBaoCreationResponse getThongBaoById(Long id) {

        ThongBao thongBao = thongBaoRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.THONG_BAO_NOT_FOUND));
        return thongBaoMapper.toThongBaoCreationResponse(thongBao);

    }
}

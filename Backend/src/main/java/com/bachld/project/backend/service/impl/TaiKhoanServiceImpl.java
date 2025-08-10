package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.response.taikhoan.AnhDaiDienUploadResponse;
import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.CloudinaryService;
import com.bachld.project.backend.service.TaiKhoanService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class TaiKhoanServiceImpl implements TaiKhoanService {

    TaiKhoanRepository taiKhoanRepository;
    CloudinaryService cloudinaryService;

    @PreAuthorize("isAuthenticated()")
    @Override
    public AnhDaiDienUploadResponse uploadAnhDaiDien(MultipartFile file) throws IOException {

        var auth =  SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        String anhDaiDienUrl = cloudinaryService.upload(file);
        taiKhoan.setAnhDaiDienUrl(anhDaiDienUrl);
        taiKhoanRepository.save(taiKhoan);
        return AnhDaiDienUploadResponse.builder()
                .anhDaiDienUrl(anhDaiDienUrl)
                .build();

    }
}

package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.taikhoan.ChangePasswordRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

    @PreAuthorize("isAuthenticated()")
    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

        var auth =  SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        boolean isCorrectPassword = passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), taiKhoan.getMatKhau());
        if (!isCorrectPassword) {
            throw new ApplicationException(ErrorCode.WRONG_PASSWORD);
        }
        if(passwordEncoder.matches(changePasswordRequest.getNewPassword(), taiKhoan.getMatKhau())) {
            throw new ApplicationException(ErrorCode.OLD_PASSWORD);
        }
        taiKhoan.setMatKhau(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        taiKhoanRepository.save(taiKhoan);

    }

}

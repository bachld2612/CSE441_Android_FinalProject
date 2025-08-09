package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.GiangVienMapper;
import com.bachld.project.backend.repository.BoMonRepository;
import com.bachld.project.backend.repository.GiangVienRepository;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.GiangVienService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class GiangVienServiceImpl implements GiangVienService {

    GiangVienRepository giangVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoMonRepository boMonRepository;
    private final GiangVienMapper giangVienMapper;
    @PreAuthorize("hasAnyAuthority('SCOPE_TRO_LY_KHOA', 'SCOPE_ADMIN')")
    @Override
    public GiangVienCreationResponse createGiangVien(GiangVienCreationRequest giangVienCreationRequest) {

        if(giangVienRepository.existsByMaGV(giangVienCreationRequest.getMaGV())) {
            throw new ApplicationException(ErrorCode.MA_GV_EXISTED);
        }
        if(taiKhoanRepository.existsByEmail((giangVienCreationRequest.getEmail()))) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan currentUser = taiKhoanRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .email(giangVienCreationRequest.getEmail())
                .matKhau(passwordEncoder.encode(giangVienCreationRequest.getMatKhau()))
                .vaiTro(Role.GIANG_VIEN)
                .build();

        BoMon boMon = boMonRepository.findById(giangVienCreationRequest.getBoMonId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.BO_MON_NOT_FOUND));

        GiangVien giangVien = GiangVien.builder()
                .hocVi(giangVienCreationRequest.getHocVi())
                .hocHam(giangVienCreationRequest.getHocHam())
                .maGV(giangVienCreationRequest.getMaGV())
                .hoTen(giangVienCreationRequest.getHoTen())
                .boMon(boMon)
                .soDienThoai(giangVienCreationRequest.getSoDienThoai())
                .taiKhoan(taiKhoan)
                .build();

        if(currentUser.getVaiTro() == Role.ADMIN){
            taiKhoan.setVaiTro(Role.TRO_LY_KHOA);
        }
        taiKhoan.setGiangVien(giangVien);
        taiKhoanRepository.save(taiKhoan);
        return giangVienMapper.toGiangVienCreationResponse(giangVienRepository.save(giangVien));

    }
}

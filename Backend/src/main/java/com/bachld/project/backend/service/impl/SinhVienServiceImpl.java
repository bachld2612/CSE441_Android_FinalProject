package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
import com.bachld.project.backend.entity.Lop;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.TaiKhoan;
import com.bachld.project.backend.enums.Role;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.SinhVienMapper;
import com.bachld.project.backend.repository.LopRepository;
import com.bachld.project.backend.repository.SinhVienRepository;
import com.bachld.project.backend.repository.TaiKhoanRepository;
import com.bachld.project.backend.service.SinhVienService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class SinhVienServiceImpl implements SinhVienService {

    SinhVienRepository sinhVienRepository;
    LopRepository lopRepository;
    PasswordEncoder passwordEncoder;
    SinhVienMapper sinhVienMapper;
    TaiKhoanRepository taiKhoanRepository;


    @PreAuthorize("hasAuthority('SCOPE_TRO_LY_KHOA')")
    @Override
    public SinhVienCreationResponse createSinhVien(SinhVienCreationRequest request) {

        if(taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }
        if(sinhVienRepository.existsByMaSV(request.getMaSV())) {
            throw new ApplicationException(ErrorCode.MA_SV_EXISTED);
        }

        Lop lop = lopRepository.findById(request.getLopId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.LOP_NOT_FOUND));

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .vaiTro(Role.SINH_VIEN)
                .build();

        SinhVien sinhVien = SinhVien.builder()
                .hoTen(request.getHoTen())
                .maSV(request.getMaSV())
                .kichHoat(true)
                .lop(lop)
                .taiKhoan(taiKhoan)
                .soDienThoai(request.getSoDienThoai())
                .build();
        taiKhoan.setSinhVien(sinhVien);
        taiKhoanRepository.save(taiKhoan);
        return sinhVienMapper.toSinhVienCreationResponse(sinhVienRepository.save(sinhVien));

    }

}

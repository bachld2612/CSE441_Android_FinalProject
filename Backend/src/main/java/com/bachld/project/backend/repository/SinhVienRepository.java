package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {

    Optional<SinhVien> findByTaiKhoan_Email(String taiKhoanEmail);
    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaSV(String maSV);

}

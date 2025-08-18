package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {
    Optional<GiangVien> findByTaiKhoan_EmailIgnoreCase(String email);
    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaGV(String maGV);
    Optional<GiangVien> findByTaiKhoan_Email(String taiKhoanEmail);

    Optional<GiangVien> findByMaGV(String maGV);
}

package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinhVienRepository extends JpaRepository<SinhVien,Long> {
    boolean existsByMaSinhVienIgnoreCase(String maSinhVien);

    boolean existsByEmailIgnoreCase(String email);

    boolean findSinhVienById(Long id);
}

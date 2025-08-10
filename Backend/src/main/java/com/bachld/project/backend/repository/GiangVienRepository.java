package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {

    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaGV(String maGV);

}

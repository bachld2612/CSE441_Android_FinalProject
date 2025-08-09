package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeTai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeTaiRepository extends JpaRepository<DeTai, Long> {

    boolean existsByTenDeTaiIgnoreCase(String tenDeTai);

    // XÓA dòng dưới nếu không có cột/field maDeTai
    // Optional<DeTai> findByMaDeTai(String maDeTai);

    // (tuỳ chọn) thay bằng tìm theo tên nếu bạn cần:
    Optional<DeTai> findByTenDeTaiIgnoreCase(String tenDeTai);
}

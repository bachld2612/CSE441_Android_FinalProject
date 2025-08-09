package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeTai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeTaiRepository extends JpaRepository<DeTai, Long> {

    boolean existsByTenDeTaiIgnoreCase(String tenDeTai);

    Optional<DeTai> findByMaDeTai(String maDeTai);
}

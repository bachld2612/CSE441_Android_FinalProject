package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.Diem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiemRepository extends JpaRepository<Diem, Long> {
    Optional<Diem> findByMaGVAndMaDeTai(String maGV, Long maDeTai);
}

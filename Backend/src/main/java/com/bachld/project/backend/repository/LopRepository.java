package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.Lop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LopRepository extends JpaRepository<Lop, Long> {

    boolean existsByTenLopIgnoreCase(String tenLop);

    Optional<Lop> findByTenLop(String tenLop);
}

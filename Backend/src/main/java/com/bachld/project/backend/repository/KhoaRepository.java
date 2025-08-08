package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhoaRepository extends JpaRepository<Khoa,Long> {

    boolean existsByTenKhoaIgnoreCase(String tenKhoa);

}

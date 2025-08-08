package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.Nganh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NganhRepository extends JpaRepository<Nganh, Long> {

    boolean existsByTenNganhIgnoreCase(String tenNganh);

}

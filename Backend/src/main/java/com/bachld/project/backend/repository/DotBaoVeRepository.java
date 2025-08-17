package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DotBaoVe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotBaoVeRepository extends JpaRepository<DotBaoVe, Long> {

    boolean existsByTenDotBaoVe(String tenDotBaoVe);

    boolean existsByTenDotBaoVeAndIdNot(String tenDotBaoVe, Long id);
}

package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.BoMon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoMonRepository extends JpaRepository<BoMon, Long> {

    boolean existsByTenBoMonIgnoreCase(String tenBoMon);

    Optional<BoMon> findByTenBoMon(String tenBoMon);
}

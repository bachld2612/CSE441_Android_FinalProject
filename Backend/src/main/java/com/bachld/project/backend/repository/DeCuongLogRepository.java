package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeCuongLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeCuongLogRepository extends JpaRepository<DeCuongLog, Long> {
    List<DeCuongLog> findByDeCuong_IdOrderByCreatedAtAsc(Long deCuongId);

}

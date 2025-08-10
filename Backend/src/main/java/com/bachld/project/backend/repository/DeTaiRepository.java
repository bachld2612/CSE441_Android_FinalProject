package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiRepository extends JpaRepository<DeTai, Long> {
    boolean existsBySinhVienThucHien_Id(Long sinhVienId);

    Page<DeTai> findByGvhd_IdAndTrangThai(Long gvhdId, DeTaiState trangThai, Pageable pageable);
}

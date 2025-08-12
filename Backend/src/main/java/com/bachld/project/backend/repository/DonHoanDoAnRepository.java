package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DonHoanDoAn;
import com.bachld.project.backend.enums.HoanState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonHoanDoAnRepository extends JpaRepository<DonHoanDoAn, Long> {
    boolean existsBySinhVien_IdAndTrangThai(Long sinhVienId, HoanState trangThai);
    Page<DonHoanDoAn> findBySinhVien_Id(Long sinhVienId, Pageable pageable);
}
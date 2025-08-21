package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeTai;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeTaiRepository extends JpaRepository<DeTai, Long> {

    boolean existsByTenDeTaiIgnoreCase(String tenDeTai);
    Optional<DeTai> findByTenDeTaiIgnoreCase(String tenDeTai);
    boolean existsBySinhVienThucHien_Id(Long sinhVienId);
    Page<DeTai> findByGvhd_IdAndTrangThai(Long gvhdId, DeTaiState trangThai, Pageable pageable);
    Optional<DeTai> findBySinhVienThucHien_Id(Long sinhVienId);
    Optional<DeTai> findByTenDeTaiIgnoreCaseAndSinhVienThucHien_MaSVIgnoreCase(String tenDeTai, String sinhVienThucHienMaSV);
    Optional<DeTai> findBySinhVienThucHien_TaiKhoan_EmailIgnoreCase(String email);
}

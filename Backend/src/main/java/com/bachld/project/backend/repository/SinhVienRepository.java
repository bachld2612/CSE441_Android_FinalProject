package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {
    Optional<SinhVien> findByTaiKhoan_EmailIgnoreCase(String email);
    Optional<SinhVien> findByTaiKhoan_Email(String taiKhoanEmail);
    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaSV(String maSV);
    Page<SinhVien> findByDeTai_Gvhd_Id(Long gvhdId, Pageable pageable);
    Page<SinhVien> findByDeTai_Gvhd_IdAndDeTai_TrangThai(Long gvhdId, DeTaiState trangThai, Pageable pageable);

    Page<SinhVien> findAllByHoTenContainingIgnoreCaseOrMaSVContainingIgnoreCase(String hoTen, String maSV, Pageable pageable);

    Optional<SinhVien> findByMaSV(String maSV);
}

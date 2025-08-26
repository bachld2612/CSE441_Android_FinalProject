package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.enums.DeTaiState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {
    Optional<SinhVien> findByTaiKhoan_EmailIgnoreCase(String email);
    Optional<SinhVien> findByTaiKhoan_Email(String taiKhoanEmail);
    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaSV(String maSV);
    Page<SinhVien> findByDeTai_Gvhd_IdAndDeTai_DotBaoVe(Long gvhdId, DotBaoVe dotBaoVe, Pageable pageable);
    Page<SinhVien> findByDeTai_Gvhd_IdAndDeTai_TrangThaiAndDeTai_DotBaoVe(Long gvhdId, DeTaiState trangThai,DotBaoVe dotBaoVe, Pageable pageable);

    Page<SinhVien> findAllByHoTenContainingIgnoreCaseOrMaSVContainingIgnoreCase(String hoTen, String maSV, Pageable pageable);

    Optional<SinhVien> findByMaSV(String maSV);


    List<SinhVien> findAllByDeTaiIsNullAndKichHoatTrue();

    List<SinhVien> findByDeTai_Gvhd_IdAndDeTai_DotBaoVeOrderByHoTenAsc(
            Long gvhdId, DotBaoVe dotBaoVe
    );

    @Query("""
        SELECT sv FROM SinhVien sv
        JOIN sv.deTai dt
        LEFT JOIN sv.lop l
        WHERE dt.gvhd.id = :gvhdId
          AND dt.dotBaoVe = :dotBaoVe
          AND (
               LOWER(sv.hoTen)      LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(sv.maSV)       LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(COALESCE(sv.soDienThoai, '')) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(COALESCE(l.tenLop, ''))      LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(COALESCE(dt.tenDeTai, ''))   LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY sv.hoTen ASC
        """)
    List<SinhVien> searchMySupervisedAll(
            @Param("gvhdId") Long gvhdId,
            @Param("dotBaoVe") DotBaoVe dotBaoVe,
            @Param("q") String q
    );
}

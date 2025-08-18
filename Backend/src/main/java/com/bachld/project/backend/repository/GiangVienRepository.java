package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.GiangVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {
    Optional<GiangVien> findByTaiKhoan_EmailIgnoreCase(String email);
    boolean existsByTaiKhoan_Email(String email);
    boolean existsByMaGV(String maGV);
    Optional<GiangVien> findByTaiKhoan_Email(String taiKhoanEmail);
    List<GiangVien> findByBoMon_Id(Long boMonId);
    List<GiangVien> findByBoMon_IdOrderByHoTenAsc(Long boMonId);
    @EntityGraph(attributePaths = {"boMon", "taiKhoan"})
    Page<GiangVien> findAll(Pageable pageable);

    @Query("SELECT gv FROM GiangVien gv " +
            "WHERE gv.boMon.id = :boMonId " +
            "AND (" +
            "   SELECT COUNT(d) FROM DeTai d " +
            "   WHERE d.gvhd = gv " +
            "   AND d.sinhVienThucHien.kichHoat = true" +
            ") < 10")
    Set<GiangVien> findAvailableGiangVienByBoMon(@Param("boMonId") Long boMonId);

    Optional<GiangVien> findByMaGV(String maGV);

    @Query("SELECT COUNT(d) FROM DeTai d " +
            "WHERE d.gvhd.maGV = :maGV " +
            "AND d.sinhVienThucHien.kichHoat = true")
    int countDeTaiByGiangVienAndSinhVienActive(@Param("maGV") String maGV);
}

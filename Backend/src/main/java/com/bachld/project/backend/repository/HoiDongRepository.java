package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.enums.HoiDongType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface HoiDongRepository extends JpaRepository<HoiDong, Long> {
    Page<HoiDong> findByLoaiHoiDongAndTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByLoaiHoiDongAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(LocalDate to1, LocalDate to2, Pageable pageable);

    Set<HoiDong> findBydeTaiSet_Id(Long deTaiSetId);

    @Query("""
        SELECT DISTINCT h
        FROM HoiDong h
        JOIN h.thanhVienHoiDongSet tv
        JOIN tv.dotBaoVeGiangVien dbv
        JOIN dbv.giangVien gv
        WHERE gv.maGV = :maGV
        AND h.id IN :hoiDongIds
    """)
    List<HoiDong> findHoiDongsByMaGVAndIds(
            @Param("maGV") String maGV,
            @Param("hoiDongIds") Collection<Long> hoiDongIds
    );

}

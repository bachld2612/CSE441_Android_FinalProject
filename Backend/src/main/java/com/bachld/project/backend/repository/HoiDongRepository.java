package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.enums.HoiDongType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HoiDongRepository extends JpaRepository<HoiDong, Long> {
    Page<HoiDong> findByLoaiHoiDongAndTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByLoaiHoiDongAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByDotBaoVe_Id(Long dotBaoVeId, Pageable pageable);

    Page<HoiDong> findByDotBaoVe_IdAndLoaiHoiDong(Long dotBaoVeId, HoiDongType loai, Pageable pageable);

    Page<HoiDong> findByDotBaoVe_IdAndTenHoiDongContainingIgnoreCase(Long dotBaoVeId, String keyword, Pageable pageable);

    Page<HoiDong> findByDotBaoVe_IdAndLoaiHoiDongAndTenHoiDongContainingIgnoreCase(Long dotBaoVeId, HoiDongType loai, String keyword, Pageable pageable);

    boolean existsByDotBaoVe_IdAndLoaiHoiDongAndDeTaiSet_IdAndIdNot(
            Long dotBaoVeId, HoiDongType loai, Long deTaiId, Long excludeHoiDongId
    );

    Optional<HoiDong> findFirstByDotBaoVe_IdAndLoaiHoiDongAndDeTaiSet_IdAndIdNot(
            Long dotBaoVeId, HoiDongType loai, Long deTaiId, Long excludeHoiDongId
    );

    boolean existsByDeTaiSet_Id(Long deTaiId);

    boolean existsByDeTaiSet_IdAndIdNot(Long deTaiId, Long hoiDongId);

    @Query("""
        select distinct hd from HoiDong hd
        left join fetch hd.thanhVienHoiDongSet tv
        left join fetch tv.dotBaoVeGiangVien dvgv
        left join fetch dvgv.giangVien gv
        left join fetch hd.deTaiSet dt
        left join fetch dt.sinhVienThucHien sv
        left join fetch sv.lop l
        left join fetch dt.gvhd gvhd
        left join fetch gvhd.boMon bm
        where hd.id = :id
""")
    Optional<HoiDong> fetchDetail(@Param("id") Long id);
}

package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.enums.HoiDongType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HoiDongRepository extends JpaRepository<HoiDong, Long> {
    Page<HoiDong> findByLoaiHoiDongAndTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByLoaiHoiDongAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(HoiDongType loai, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(String keyword, LocalDate to1, LocalDate to2, Pageable pageable);

    Page<HoiDong> findByThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(LocalDate to1, LocalDate to2, Pageable pageable);
}

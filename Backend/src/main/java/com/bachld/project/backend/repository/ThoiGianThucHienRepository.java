package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.enums.CongViec;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThoiGianThucHienRepository extends JpaRepository<ThoiGianThucHien, Long> {

    // Các mốc (WINDOW) đang mở cho 1 công việc tại 1 ngày
    @EntityGraph(attributePaths = {"dotBaoVe"})
    List<ThoiGianThucHien> findAllByCongViecAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
            CongViec congViec, LocalDate today1, LocalDate today2
    );

    Optional<ThoiGianThucHien> findByDotBaoVe_IdAndCongViec(Long dotBaoVeId, CongViec congViec);
}

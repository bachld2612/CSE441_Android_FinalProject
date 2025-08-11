package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.enums.CongViec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThoiGianThucHienRepository extends JpaRepository<ThoiGianThucHien, Long> {
    // Lấy mốc thời gian theo đợt & loại công việc
    Optional<ThoiGianThucHien> findByDotBaoVe_IdAndCongViec(Long dotBaoVeId, CongViec congViec);

    // Tìm tất cả mốc NỘP_DE_CƯƠNG đang “mở” tại một ngày (phục vụ export theo “đợt đang mở”)
    List<ThoiGianThucHien> findAllByCongViecAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
            CongViec congViec, LocalDate from, LocalDate to
    );
}

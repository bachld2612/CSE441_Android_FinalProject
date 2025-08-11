package com.bachld.project.backend.repository;


import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.enums.CongViec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThoiGianThucHienRepository extends JpaRepository<ThoiGianThucHien, Long> {


    boolean existsByDotBaoVeAndCongViec(DotBaoVe dotBaoVe, CongViec congViec);
}

package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.ThanhVienHoiDong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThanhVienHoiDongRepository extends JpaRepository<ThanhVienHoiDong, Long> {
    List<ThanhVienHoiDong> findByHoiDong_Id(Long hoiDongId);
}
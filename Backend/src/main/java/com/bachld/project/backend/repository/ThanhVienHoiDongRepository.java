package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.ThanhVienHoiDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ThanhVienHoiDongRepository extends JpaRepository<ThanhVienHoiDong, Long> {

    @Query("""
        SELECT COUNT(tv) > 0
        FROM ThanhVienHoiDong tv
        JOIN tv.dotBaoVeGiangVien dbv
        JOIN dbv.giangVien gv
        WHERE gv.maGV = :maGV
        AND tv.hoiDong.id IN :hoiDongIds
    """)
    boolean existsByMaGVAndHoiDongIds(
            @Param("maGV") String maGV,
            @Param("hoiDongIds") Collection<Long> hoiDongIds
    );

    List<ThanhVienHoiDong> findByHoiDong_Id(Long hoiDongId);
}
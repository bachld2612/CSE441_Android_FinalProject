package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DotBaoVeGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DotBaoVeGiangVienRepository extends JpaRepository<DotBaoVeGiangVien, Long> {
    Optional<DotBaoVeGiangVien> findByDotBaoVe_IdAndGiangVien_Id(Long dotBaoVeId, Long giangVienId);
}

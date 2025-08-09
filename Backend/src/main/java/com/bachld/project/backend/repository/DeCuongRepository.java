package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeCuong;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DeCuongRepository extends JpaRepository<DeCuong, Long> {

    boolean existsByDeTaiId(Long deTaiId);

    Optional<DeCuong> findByDeTaiId(Long deTaiId);

    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCase(String email, Pageable pageable);

    // Để tránh N+1 khi phân trang dùng cho giảng viên xem danh sách mình huớng dẫn để duyệt đề cương
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findAll(Pageable pageable);
}

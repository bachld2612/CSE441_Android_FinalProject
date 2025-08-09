package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.DeCuong;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DeCuongRepository extends JpaRepository<DeCuong, Long> {

    // ✅ dùng property path theo quan hệ deTai
    boolean existsByDeTai_Id(Long deTaiId);

    Optional<DeCuong> findByDeTai_Id(Long deTaiId);

    Page<DeCuong> findByDeTai_Gvhd_Id(Long gvId, Pageable pageable);                 // danh sách theo GVHD
    Page<DeCuong> findByDeTai_SinhVienThucHien_Id(Long svId, Pageable pageable);

    // khi cần lấy entity để cập nhật, load kèm các quan hệ cần thiết
    @EntityGraph(attributePaths = {"deTai", "deTai.gvhd", "deTai.gvhd.taiKhoan"})
    Optional<DeCuong> findWithGraphById(Long id);

    // ✅ danh sách đề cương của các SV do GV này hướng dẫn
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCase(String email, Pageable pageable);

    // Admin/TBM xem tất cả (tránh N+1)
    @Override
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findAll(Pageable pageable);
}

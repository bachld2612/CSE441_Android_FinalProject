package com.bachld.project.backend.repository;

import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.DeCuong;
import com.bachld.project.backend.enums.DeCuongState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DeCuongRepository extends JpaRepository<DeCuong, Long> {

    Optional<DeCuong> findByDeTai_Id(Long deTaiId);

    // ✅ danh sách đề cương của các SV do GV này hướng dẫn
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCase(String email, Pageable pageable);

    // Admin/TBM xem tất cả (tránh N+1)
    @Override
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findAll(Pageable pageable);

    // ✅ TBM: danh sách đề cương đã duyệt theo bộ môn quản lý
    @EntityGraph(attributePaths = {
            "deTai",
            "deTai.sinhVienThucHien",
            "deTai.sinhVienThucHien.lop",
            "deTai.gvhd",
            "deTai.boMonQuanLy"
    })
    Page<DeCuong> findByTrangThaiAndDeTai_BoMonQuanLy_Id(
            DeCuongState trangThai, Long boMonId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "deTai",
            "deTai.sinhVienThucHien",
            "deTai.sinhVienThucHien.lop",
            "deTai.gvhd",
            "deTai.boMonQuanLy"
    })
    List<DeCuong> findByTrangThaiAndDeTai_BoMonQuanLy_Id(
            DeCuongState trangThai, Long boMonId);

}

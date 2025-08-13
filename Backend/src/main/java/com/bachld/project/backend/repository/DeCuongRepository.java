package com.bachld.project.backend.repository;

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

    // Admin/TBM xem tất cả (tránh N+1)
    @Override
    @EntityGraph(attributePaths = { "deTai", "deTai.sinhVienThucHien", "deTai.gvhd" })
    Page<DeCuong> findAll(Pageable pageable);

    // ====== CÁC HÀM MỚI: lọc theo danh sách đợt đang mở ======

    @EntityGraph(attributePaths = {
            "deTai","deTai.sinhVienThucHien","deTai.sinhVienThucHien.lop","deTai.gvhd","deTai.boMonQuanLy"
    })
    Page<DeCuong> findByDeTai_Gvhd_TaiKhoan_EmailIgnoreCaseAndDeTai_DotBaoVe_IdIn(
            String email, List<Long> dotIds, Pageable pageable);

    @EntityGraph(attributePaths = {
            "deTai","deTai.sinhVienThucHien","deTai.sinhVienThucHien.lop","deTai.gvhd","deTai.boMonQuanLy"
    })
    Page<DeCuong> findByDeTai_DotBaoVe_IdIn(List<Long> dotIds, Pageable pageable);

    @EntityGraph(attributePaths = {
            "deTai","deTai.sinhVienThucHien","deTai.sinhVienThucHien.lop","deTai.gvhd","deTai.boMonQuanLy"
    })
    Page<DeCuong> findByTrangThaiAndDeTai_BoMonQuanLy_IdAndDeTai_DotBaoVe_IdIn(
            DeCuongState trangThai, Long boMonId, List<Long> dotIds, Pageable pageable);

    @EntityGraph(attributePaths = {
            "deTai","deTai.sinhVienThucHien","deTai.sinhVienThucHien.lop","deTai.gvhd","deTai.boMonQuanLy"
    })
    List<DeCuong> findByTrangThaiAndDeTai_BoMonQuanLy_IdAndDeTai_DotBaoVe_IdIn(
            DeCuongState trangThai, Long boMonId, List<Long> dotIds);

    Optional<DeCuong> findByDeTai_SinhVienThucHien_TaiKhoan_EmailIgnoreCase(String email);
}

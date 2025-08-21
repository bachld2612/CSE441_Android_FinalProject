package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.DeTaiSinhVienApprovalResponse;
import com.bachld.project.backend.dto.response.giangvien.SinhVienSupervisedResponse;
import com.bachld.project.backend.dto.response.sinhvien.GetSinhVienWithoutDeTaiResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienInfoResponse;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienResponse;
import com.bachld.project.backend.entity.Lop;
import com.bachld.project.backend.entity.SinhVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface SinhVienMapper {
    @Mapping(target = "taiKhoan.email",  source = "email")
    @Mapping(target = "taiKhoan.vaiTro", expression = "java(com.bachld.project.backend.enums.Role.SINH_VIEN)")
    @Mapping(target = "lop", source = "lopId")
    SinhVien toSinhVien(SinhVienCreationRequest request);

    @Mapping(source = "taiKhoan.email", target = "email")
    @Mapping(source = "lop", target = "lopId")
    SinhVienCreationResponse toSinhVienCreationResponse(SinhVien sinhVien);

    @Mapping(source = "taiKhoan.email", target = "email")
    @Mapping(source = "lop.tenLop", target = "tenLop")
    SinhVienResponse toSinhVienResponse(SinhVien sinhVien);

    @Mapping(source = "lop.tenLop", target = "tenLop")
    @Mapping(source = "deTai.tenDeTai", target = "tenDeTai")
    SinhVienSupervisedResponse toSinhVienSupervisedResponse(SinhVien sv);

    @Mapping(source = "lop.tenLop", target = "tenLop")
    @Mapping(source = "deTai.tenDeTai", target = "tenDeTai")
    @Mapping(source = "deTai.trangThai", target = "trangThai")
    @Mapping(source = "deTai.id", target = "idDeTai")
    @Mapping(source = "deTai.tongQuanDeTaiUrl", target = "tongQuanDeTaiUrl")
    DeTaiSinhVienApprovalResponse toDeTaiSinhVienApprovalResponse(SinhVien sv);

    @Mapping(source = "lop.tenLop", target = "tenLop")
    @Mapping(source = "taiKhoan.email", target = "email")
    @Mapping(source = "lop.nganh.khoa.tenKhoa", target = "tenKhoa")
    @Mapping(source = "lop.nganh.tenNganh", target = "tenNganh")
    SinhVienInfoResponse toSinhVienInfoResponse(SinhVien sv);

    GetSinhVienWithoutDeTaiResponse toGetSinhVienWithoutDeTaiResponse(SinhVien sv);

    default Lop map(Long lopId) {
        if (lopId == null) return null;
        Lop lop = new Lop();
        lop.setId(lopId);
        return lop;
    }

    default Long map(Lop lop) {
        return lop != null ? lop.getId() : null;
    }

}

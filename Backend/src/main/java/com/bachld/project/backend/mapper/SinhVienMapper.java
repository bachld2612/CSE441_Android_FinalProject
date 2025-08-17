package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.sinhvien.SinhVienCreationRequest;
import com.bachld.project.backend.dto.response.sinhvien.SinhVienCreationResponse;
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

package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.giangvien.GiangVienCreationRequest;
import com.bachld.project.backend.dto.response.giangvien.GiangVienCreationResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienInfoResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienLiteResponse;
import com.bachld.project.backend.dto.response.giangvien.GiangVienResponse;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface GiangVienMapper {

    @Mapping(target = "taiKhoan.email", source = "email")
    @Mapping(target = "taiKhoan.matKhau", ignore = true)
    @Mapping(target = "boMon", source = "boMonId")
    GiangVien toGiangVien(GiangVienCreationRequest request);

    @Mapping(source = "taiKhoan.email", target = "email")
    @Mapping(source = "taiKhoan.vaiTro", target = "vaiTro")
    @Mapping(source = "boMon", target = "boMonId")
    GiangVienCreationResponse toGiangVienCreationResponse(GiangVien entity);

    GiangVienInfoResponse toGiangVienInfoResponse(GiangVien entity);

    GiangVienLiteResponse toLite(GiangVien entity);

    @Mapping(source = "taiKhoan.email", target = "email")
    @Mapping(source = "boMon.id",       target = "boMonId")
    GiangVienResponse toGiangVienResponse(GiangVien entity);

    default BoMon map(Long boMonId) {
        if (boMonId == null) return null;
        BoMon bm = new BoMon();
        bm.setId(boMonId);
        return bm;
    }

    default Long map(BoMon boMon) {
        return boMon != null ? boMon.getId() : null;
    }


    default Role mapRoleDefault(Role vaiTro) {
        return vaiTro != null ? vaiTro : Role.GIANG_VIEN;
    }

}

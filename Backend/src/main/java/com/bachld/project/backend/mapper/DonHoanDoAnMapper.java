package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.response.donhoandoan.DonHoanDoAnResponse;
import com.bachld.project.backend.entity.DonHoanDoAn;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.SinhVien;
import com.bachld.project.backend.entity.TaiKhoan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DonHoanDoAnMapper {

    @Mapping(source = "sinhVien.id", target = "sinhVienId")
    @Mapping(source = "nguoiPheDuyet.id", target = "nguoiPheDuyetId")
    DonHoanDoAnResponse toResponse(DonHoanDoAn entity);

    default Long toId(SinhVien x) { return x != null ? x.getId() : null; }
    default Long toId(TaiKhoan x) { return x != null ? x.getId() : null; }
}

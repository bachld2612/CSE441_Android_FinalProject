package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.thoigianthuchien.ThoiGianThucHienRequest;
import com.bachld.project.backend.dto.response.thoigianthuchien.ThoiGianThucHienResponse;
import com.bachld.project.backend.entity.ThoiGianThucHien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ThoiGianThucHienMapper {

    @Mapping(target = "tenDotBaoVe", source = "dotBaoVe.tenDotBaoVe")
    ThoiGianThucHienResponse toThoiGianThucHienResponse(ThoiGianThucHien thoiGianThucHien);
    @Mapping(target = "dotBaoVe.id", source = "dotBaoVeId")
    ThoiGianThucHien toThoiGianThucHien(ThoiGianThucHienRequest thoiGianThucHienRequest);

    void updateThoiGianThucHienFromDto(ThoiGianThucHienRequest thoiGianThucHienRequest,@MappingTarget ThoiGianThucHien entity);

}

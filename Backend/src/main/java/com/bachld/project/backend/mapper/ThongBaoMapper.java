package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.thongbao.ThongBaoCreationRequest;
import com.bachld.project.backend.dto.response.thongbao.ThongBaoCreationResponse;
import com.bachld.project.backend.entity.ThongBao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ThongBaoMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    ThongBaoCreationResponse toThongBaoCreationResponse(ThongBao entity);
    ThongBao toThongBao(ThongBaoCreationRequest thongBaoCreationRequest);

}

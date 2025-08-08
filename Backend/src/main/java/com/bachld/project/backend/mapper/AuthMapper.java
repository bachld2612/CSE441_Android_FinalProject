package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.response.auth.InfoResponse;
import com.bachld.project.backend.entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface AuthMapper {


}

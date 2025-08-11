package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.dotbaove.DotBaoVeRequest;
import com.bachld.project.backend.dto.response.dotbaove.DotBaoVeResponse;
import com.bachld.project.backend.entity.DotBaoVe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface DotBaoVeMapper {

    DotBaoVeResponse toDotBaoVeResponse(DotBaoVe request);
    DotBaoVe toDotBaoVe(DotBaoVeRequest request);

    void updateDotBaoVeFromDto(DotBaoVeRequest request, @MappingTarget DotBaoVe entity);

}

package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.khoa.KhoaRequest;
import com.bachld.project.backend.dto.response.khoa.KhoaResponse;
import com.bachld.project.backend.entity.Khoa;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface KhoaMapper {
    KhoaResponse toKhoaResponse(Khoa khoa);
    Khoa toKhoa(KhoaRequest khoaRequest);
}

package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.nganh.NganhRequest;
import com.bachld.project.backend.dto.response.nganh.NganhResponse;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.entity.Nganh;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface NganhMapper {

    @Mapping(source = "khoa", target = "khoaId")
    NganhResponse toNganhResponse(Nganh nganh);

    @Mapping(source = "khoaId", target = "khoa")
    Nganh toNganh(NganhRequest nganhRequest);

    default Long toId(Khoa khoa) {
        return (khoa != null) ? khoa.getId() : null;
    }

    default Khoa toKhoa(Long id) {
        if (id == null) return null;
        Khoa k = new Khoa();
        k.setId(id);
        return k;
    }
}

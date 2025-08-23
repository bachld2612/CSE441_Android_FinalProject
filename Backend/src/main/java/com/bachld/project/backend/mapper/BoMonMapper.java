package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.bomon.BoMonRequest;
import com.bachld.project.backend.dto.response.bomon.BoMonResponse;
import com.bachld.project.backend.dto.response.bomon.BoMonWithTruongBoMonResponse;
import com.bachld.project.backend.dto.response.bomon.TruongBoMonCreationResponse;
import com.bachld.project.backend.entity.BoMon;
import com.bachld.project.backend.entity.Khoa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface BoMonMapper {
    @Mapping(source = "khoaId", target = "khoa")
    BoMon toBoMon(BoMonRequest request);
    @Mapping(source = "khoa", target = "khoaId")
    BoMonResponse toBoMonResponse(BoMon boMon);

    @Mapping(target = "maGV",   source = "truongBoMon.maGV")
    @Mapping(target = "hoTen",  source = "truongBoMon.hoTen")
    @Mapping(target = "hocVi",  source = "truongBoMon.hocVi")
    @Mapping(target = "hocHam", source = "truongBoMon.hocHam")
    @Mapping(target = "tenBoMon", source = "tenBoMon")
    TruongBoMonCreationResponse toTruongBoMonCreationResponse(BoMon boMon);

    @Mapping(target = "khoaId", source = "khoa.id")
    @Mapping(target = "tenKhoa", source = "khoa.tenKhoa")
    @Mapping(target = "truongBoMonHoTen",
            expression = "java(boMon.getTruongBoMon() != null ? boMon.getTruongBoMon().getHoTen() : null)")
    BoMonWithTruongBoMonResponse toWithTruongBoMon(BoMon boMon);

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

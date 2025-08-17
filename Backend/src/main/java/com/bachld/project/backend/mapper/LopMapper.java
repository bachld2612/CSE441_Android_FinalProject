package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.lop.LopRequest;
import com.bachld.project.backend.dto.response.lop.LopResponse;
import com.bachld.project.backend.entity.Khoa;
import com.bachld.project.backend.entity.Lop;
import com.bachld.project.backend.entity.Nganh;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface LopMapper {

    @Mapping(source = "nganh", target = "nganhId")
    LopResponse toLopResponse(Lop lop);
    @Mapping(source = "nganhId", target = "nganh")
    Lop toLop(LopRequest lopRequest);

    default Long toId(Nganh nganh) {
        return (nganh != null) ? nganh.getId() : null;
    }

    default Nganh toNganh(Long id) {
        if (id == null) return null;
        Nganh nganh = new Nganh();
        nganh.setId(id);
        return nganh;
    }

}

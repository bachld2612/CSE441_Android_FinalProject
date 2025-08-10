package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.detai.DeTaiRequest;
import com.bachld.project.backend.dto.response.detai.DeTaiResponse;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.SinhVien;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface DeTaiMapper {

    // Request -> Entity
    @Mapping(source = "gvhdId", target = "gvhd")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "nhanXet", ignore = true)
    @Mapping(target = "sinhVienThucHien", ignore = true)
    @Mapping(target = "dotBaoVeDeTai", ignore = true)
    @Mapping(target = "tongQuanDeTaiUrl", ignore = true)
    DeTai toDeTai(DeTaiRequest request);

    // Entity -> Response
    @Mapping(source = "gvhd", target = "gvhdId")
    @Mapping(source = "sinhVienThucHien", target = "sinhVienId")
    DeTaiResponse toDeTaiResponse(DeTai entity);

    // Convert GiangVien and SinhVien to their IDs
    default Long toId(GiangVien gv) { return gv != null ? gv.getId() : null; }
    default Long toId(SinhVien sv) { return sv != null ? sv.getId() : null; }

    default GiangVien toGiangVien(Long id) {
        if (id == null) return null;
        GiangVien g = new GiangVien();
        g.setId(id);
        return g;
    }
}
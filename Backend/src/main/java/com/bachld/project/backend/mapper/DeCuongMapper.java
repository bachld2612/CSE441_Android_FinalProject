package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.request.decuong.DeCuongRequest;
import com.bachld.project.backend.dto.response.decuong.DeCuongResponse;
import com.bachld.project.backend.entity.DeCuong;
import com.bachld.project.backend.entity.DeTai;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DeCuongMapper {

    // Entity -> Response
    @Mappings({
            @Mapping(source = "deTai.tenDeTai",               target = "tenDeTai"),
            @Mapping(source = "deTai.sinhVienThucHien.maSV",  target = "maSV"),
            @Mapping(source = "deTai.sinhVienThucHien.hoTen", target = "hoTenSinhVien"),
            @Mapping(source = "deTai.gvhd.hoTen",             target = "hoTenGiangVien")
    })
    DeCuongResponse toResponse(DeCuong entity);

    List<DeCuongResponse> toResponse(List<DeCuong> entities);

    // Request -> Entity (tạo mới)
    @Mapping(source = "deTaiId", target = "deTai", qualifiedByName = "idToDeTai")
    @Mapping(source = "fileUrl",  target = "deCuongUrl")
    DeCuong toEntity(DeCuongRequest request);

    // Cập nhật in-place (nộp lại → set PENDING làm ở service)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "deTaiId", target = "deTai", qualifiedByName = "idToDeTai")
    @Mapping(source = "fileUrl",  target = "deCuongUrl")
    void update(@MappingTarget DeCuong target, DeCuongRequest request);

    // ===== Helpers =====
    @Named("idToDeTai")
    default DeTai idToDeTai(Long id) {
        if (id == null) return null;
        DeTai dt = new DeTai();
        dt.setId(id);
        return dt;
    }
}

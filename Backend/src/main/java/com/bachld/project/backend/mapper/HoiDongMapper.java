package com.bachld.project.backend.mapper;

import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse.SinhVienTrongHoiDong;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.entity.*;
import com.bachld.project.backend.enums.HoiDongRole;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface HoiDongMapper {

    @Mapping(source = "loaiHoiDong", target = "loaiHoiDong", qualifiedByName = "enumName")
    HoiDongListItemResponse toListItem(HoiDong entity);

    @Mapping(source = "loaiHoiDong", target = "loaiHoiDong", qualifiedByName = "enumName")

    @Mapping(target = "chuTich", ignore = true)
    @Mapping(target = "thuKy", ignore = true)
    @Mapping(target = "giangVienPhanBien", ignore = true)
    @Mapping(target = "sinhVienList", expression = "java(toSinhVienList(entity.getDeTaiSet()))")
    HoiDongDetailResponse toDetail(HoiDong entity);

    @Named("enumName")
    default String enumName(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    default List<SinhVienTrongHoiDong> toSinhVienList(Set<DeTai> deTais) {
        if (deTais == null) return List.of();
        List<SinhVienTrongHoiDong> out = new ArrayList<>();
        for (DeTai dt : deTais) {
            if (dt == null) continue;
            SinhVien sv = dt.getSinhVienThucHien();
            String lop = (sv != null && sv.getLop() != null) ? sv.getLop().getTenLop() : null;
            String gvhd = (dt.getGvhd() != null) ? dt.getGvhd().getHoTen() : null;
            String boMon = (dt.getGvhd() != null && dt.getGvhd().getBoMon() != null)
                    ? dt.getGvhd().getBoMon().getTenBoMon() : null;

            out.add(SinhVienTrongHoiDong.builder()
                    .hoTen(sv != null ? sv.getHoTen() : null)
                    .maSV(sv != null ? sv.getMaSV() : null)
                    .lop(lop)
                    .tenDeTai(dt.getTenDeTai())
                    .gvhd(gvhd)
                    .boMon(boMon)
                    .build());
        }
        return out;
    }

    @AfterMapping
    default void fillRoles(HoiDong src,
                           @MappingTarget HoiDongDetailResponse.HoiDongDetailResponseBuilder target) {
        if (src.getThanhVienHoiDongSet() == null) return;

        String chuTich = null;
        String thuKy = null;
        List<String> examiners = new ArrayList<>();

        for (ThanhVienHoiDong tv : src.getThanhVienHoiDongSet()) {
            if (tv == null) continue;
            GiangVien gv = (tv.getDotBaoVeGiangVien() != null)
                    ? tv.getDotBaoVeGiangVien().getGiangVien() : null;
            String name = (gv != null) ? gv.getHoTen() : null;
            if (name == null || name.isBlank()) continue;

            switch (tv.getChucVu()) {
                case CHAIR     -> chuTich = name;
                case SECRETARY -> thuKy = name;
                case EXAMINER  -> examiners.add(name);
            }
        }

        target.chuTich(chuTich);
        target.thuKy(thuKy);
        target.giangVienPhanBien(examiners);
    }
}

package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.HoiDongType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "hoi_dong")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoiDong extends BaseEntity {

    String tenHoiDong;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    HoiDongType loaiHoiDong;
    @ManyToMany(mappedBy = "hoiDongSet")
    Set<DotBaoVeDeTai> dotBaoVeDeTaiSet;
    @OneToMany(mappedBy = "hoiDong")
    Set<DotBaoVeGiangVienHoiDong> dotBaoVeGiangVienHoiDongSet;

}

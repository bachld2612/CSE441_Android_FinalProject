package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.HoiDongType;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    HoiDongType loaiHoiDong;
    @OneToMany(mappedBy = "hoiDong")
    Set<DotBaoVeGiangVienHoiDong> dotBaoVeGiangVienHoiDongSet;
    @ManyToMany
    @JoinTable(
            name = "hoi_dong_de_tai",
            joinColumns = @JoinColumn(name = "hoi_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "de_tai_id")
    )
    Set<DeTai> deTaiSet;

}

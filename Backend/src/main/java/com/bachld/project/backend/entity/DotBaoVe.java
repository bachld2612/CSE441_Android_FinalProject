package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "dot_bao_ve")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVe extends BaseEntity {

    String tenDotBaoVe;
    int hocKi;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    int namBatDau;
    int namKetThuc;
    @OneToMany(mappedBy = "dotBaoVe")
    Set<ThoiGianThucHien> thoiGianThucHien;
    @OneToMany(mappedBy = "dotBaoVe")
    Set<DeTai> deTaiSet;

}

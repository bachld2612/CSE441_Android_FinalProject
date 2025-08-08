package com.bachld.project.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "dot_bao_ve")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVe extends BaseEntity {

    String tenDotBaoVe;
    String hocKi;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    @OneToOne(mappedBy = "dotBaoVe")
    DotBaoVeDeTai dotBaoVeDeTai;


}

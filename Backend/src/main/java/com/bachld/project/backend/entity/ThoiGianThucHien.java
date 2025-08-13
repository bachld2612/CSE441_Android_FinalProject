package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.CongViec;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "thoi_gian_thuc_hien")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThoiGianThucHien extends  BaseEntity {

    CongViec congViec;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    @ManyToOne
    @JoinColumn(name = "dot_bao_ve_id", nullable = false)
    DotBaoVe  dotBaoVe;

}

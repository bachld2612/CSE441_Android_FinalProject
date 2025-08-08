package com.bachld.project.backend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "dot_bao_ve_giang_vien")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVeGiangVien extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "dot_bao_ve_id")
    DotBaoVe dotBaoVe;

    @OneToOne
    @JoinColumn(name = "giang_vien_id")
    GiangVien giangVien;

    @OneToMany(mappedBy = "dotBaoVeGiangVien")
    Set<DotBaoVeGiangVienHoiDong> dotBaoVeGiangVienHoiDongSet;

}

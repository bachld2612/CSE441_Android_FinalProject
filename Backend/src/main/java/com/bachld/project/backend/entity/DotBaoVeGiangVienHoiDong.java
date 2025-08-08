package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.HoiDongRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "dot_bao_ve_giang_vien_hoi_dong")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVeGiangVienHoiDong extends BaseEntity {

    @ManyToOne
    @JoinColumn
    DotBaoVeGiangVien dotBaoVeGiangVien;

    @ManyToOne
    @JoinColumn
    HoiDong hoiDong;

    @Enumerated(EnumType.STRING)
    HoiDongRole chucVu;

}

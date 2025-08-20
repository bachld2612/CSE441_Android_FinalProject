package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.HoiDongRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "thanh_vien_hoi_dong")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhVienHoiDong extends BaseEntity {

    @Enumerated(EnumType.STRING)
    HoiDongRole chucVu;

    @ManyToOne
    @JoinColumn(name = "hoi_dong_id")
    HoiDong hoiDong;

    @ManyToOne
    @JoinColumn(name = "dot_bao_ve_giang_vien_id")
    DotBaoVeGiangVien dotBaoVeGiangVien;

}

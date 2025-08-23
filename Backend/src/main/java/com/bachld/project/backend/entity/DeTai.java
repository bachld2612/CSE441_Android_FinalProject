package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.DeTaiState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "de_tai")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeTai extends BaseEntity {

    String tenDeTai;
    @Enumerated(EnumType.STRING)
    DeTaiState trangThai;
    @Column(columnDefinition = "TEXT")
    String nhanXet;
    String tongQuanDeTaiUrl;
    @OneToOne
    @JoinColumn(name = "sinh_vien_thuc_hien_id")
    SinhVien sinhVienThucHien;
    @ManyToOne
    GiangVien gvhd;
    @OneToOne(mappedBy = "deTai")
    DeCuong deCuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bo_mon_quan_ly_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    BoMon boMonQuanLy;

    @ManyToOne
    @JoinColumn(name = "dot_bao_ve_id")
    DotBaoVe dotBaoVe;

    @ManyToMany(mappedBy = "deTaiSet")
    Set<HoiDong> hoiDongSet;
}

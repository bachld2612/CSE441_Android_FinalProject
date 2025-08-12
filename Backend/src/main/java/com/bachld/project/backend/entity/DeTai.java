package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.DeTaiState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "de_tai")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    @OneToOne(mappedBy = "deTai")
    DotBaoVeDeTai dotBaoVeDeTai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bo_mon_quan_ly_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    BoMon boMonQuanLy;

    @OneToMany(mappedBy = "deTai", fetch = FetchType.LAZY)
    List<DonHoanDoAn> donHoanList = new ArrayList<>();
}

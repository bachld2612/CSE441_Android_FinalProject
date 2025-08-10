package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.DeTaiState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

}

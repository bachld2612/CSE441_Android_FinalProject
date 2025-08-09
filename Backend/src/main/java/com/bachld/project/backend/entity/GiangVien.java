package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(
        name = "giang_vien",
        indexes = {
                @Index(name="idx_sv_maGV", columnList = "maGV")
        }
    )
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiangVien extends BaseEntity {

    @Column(unique = true, nullable = false)
    String maGV;
    String hoTen;
    String soDienThoai;
    String hocVi;
    String hocHam;
    @ManyToOne
    BoMon boMon;
    @OneToMany(mappedBy = "gvhd")
    Set<DeTai> deTaiSet;
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    TaiKhoan taiKhoan;

}

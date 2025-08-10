package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Table(
        name = "sinh_vien",
        indexes = {
                @Index(name="idx_sv_maSV", columnList = "maSV")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVien extends BaseEntity {

    @Column(unique = true, nullable = false)
    String maSV;
    String hoTen;
    String soDienThoai;
    boolean kichHoat;
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    TaiKhoan taiKhoan;
    @OneToOne(mappedBy = "sinhVienThucHien")
    DeTai deTai;
    @ManyToOne
    Lop lop;

}

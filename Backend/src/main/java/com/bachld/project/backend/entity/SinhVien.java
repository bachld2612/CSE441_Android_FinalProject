package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "sinh_vien")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVien extends BaseEntity {

    @Column(unique = true, nullable = false)
    String maSV;
    String hoTen;
    String soDienThoai;
    boolean trangThai;
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    TaiKhoan taiKhoan;
    @OneToOne(mappedBy = "sinhVienThucHien")
    DeTai deTai;
    @ManyToOne
    Lop lop;
}

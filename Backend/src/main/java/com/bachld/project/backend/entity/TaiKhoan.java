package com.bachld.project.backend.entity;


import com.bachld.project.backend.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tai_khoan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaiKhoan extends BaseEntity{

    String email;
    String matKhau;
    String anhDaiDienUrl;
    Role vaiTro;
    @OneToOne(mappedBy = "taiKhoan")
    SinhVien sinhVien;
    @ManyToOne
    Lop lop;

}

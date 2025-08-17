package com.bachld.project.backend.entity;


import com.bachld.project.backend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tai_khoan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaiKhoan extends BaseEntity{

    String email;
    String matKhau;
    @Builder.Default
    String anhDaiDienUrl = "https://graph.facebook.com/100000000000000/picture?type=large";
    @Enumerated(EnumType.STRING)
    Role vaiTro;
    @OneToOne(mappedBy = "taiKhoan")
    SinhVien sinhVien;
    @OneToOne(mappedBy = "taiKhoan")
    GiangVien giangVien;

}

package com.bachld.project.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "thong_bao")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThongBao extends BaseEntity {

    String tieuDe;
    String noiDung;
    @ManyToOne
    TaiKhoan taiKhoan;

}

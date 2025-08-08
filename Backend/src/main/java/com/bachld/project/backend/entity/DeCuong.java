package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.DeCuongState;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "de_cuong")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeCuong extends BaseEntity{

    DeCuongState trangThai;
    String deCuongUrl;
    @OneToOne
    @JoinColumn(name = "de_tai_id")
    DeTai deTai;

}

package com.bachld.project.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "lop")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lop extends BaseEntity {

    String tenLop;
    @ManyToOne
    Nganh nganh;
    @OneToMany(mappedBy = "lop")
    Set<SinhVien> sinhVienSet;

}

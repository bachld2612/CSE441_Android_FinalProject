package com.bachld.project.backend.entity;

import jakarta.persistence.*;
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

    @Column(unique = true, nullable = false)
    String tenLop;
    @ManyToOne
    Nganh nganh;
    @OneToMany(mappedBy = "lop")
    Set<SinhVien> sinhVienSet;

}

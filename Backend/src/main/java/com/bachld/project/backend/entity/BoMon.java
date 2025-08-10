package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "bo_mon")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoMon extends BaseEntity {

    String tenBoMon;
    @ManyToOne
    Khoa khoa;
    @OneToMany(mappedBy = "boMon")
    Set<GiangVien> giangVienSet;
    @OneToOne
    @JoinColumn(name = "truong_bo_mon_id")
    GiangVien truongBoMon;

}

package com.bachld.project.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

}

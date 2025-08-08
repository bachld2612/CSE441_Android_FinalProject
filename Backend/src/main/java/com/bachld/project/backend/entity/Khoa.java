package com.bachld.project.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "khoa")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Khoa extends BaseEntity {

    String tenKhoa;
    @OneToMany(mappedBy = "khoa")
    Set<Nganh> nganhSet;
    @OneToMany(mappedBy = "khoa")
    Set<BoMon> boMonSet;

}

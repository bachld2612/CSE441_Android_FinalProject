package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "nganh")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Nganh extends BaseEntity {

    String tenNganh;
    @ManyToOne
    Khoa khoa;
    @OneToMany(mappedBy = "nganh")
    Set<Lop> lopSet;

}

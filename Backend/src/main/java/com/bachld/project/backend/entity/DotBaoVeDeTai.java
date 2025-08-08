package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "dot_bao_ve_de_tai")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVeDeTai extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "de_tai_id")
    DeTai deTai;

    @OneToOne
    @JoinColumn(name = "dot_bao_ve_id")
    DotBaoVe dotBaoVe;

    @ManyToMany
    @JoinTable(
            name = "dot_bao_ve_de_tai_hoi_dong",
            joinColumns = @JoinColumn(name = "dot_bao_ve_de_tai_id"),
            inverseJoinColumns = @JoinColumn(name = "hoi_dong_id")
    )
    Set<HoiDong> hoiDongSet;

}

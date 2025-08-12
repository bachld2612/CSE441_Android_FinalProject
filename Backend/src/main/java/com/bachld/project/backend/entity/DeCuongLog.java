package com.bachld.project.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "de_cuong_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeCuongLog extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    String nhanXet;
    @ManyToOne
    @JoinColumn(name = "de_cuong_id", nullable = false)
    DeCuong deCuong;

}

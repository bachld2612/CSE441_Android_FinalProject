package com.bachld.project.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "diem")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Diem extends BaseEntity{
     String maGV;
    Long maDeTai;
    String maSV;

    // điểm đại diện: Đề tài có tính cấp thiết & thực tiễn
    Double diemCapThiet;

    @Column(columnDefinition = "TEXT")
    String nhanXetChung;
}

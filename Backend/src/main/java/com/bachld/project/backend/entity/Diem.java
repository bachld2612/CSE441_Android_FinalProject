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
    String tenSV;
    String maSV;
    String tenDeTai;
    @Column(columnDefinition = "TEXT")
    String nhanXetChung;
    Double tinhThucTien;
    Double thoiGianTrinhBay;
    Double hinhVeSanPham;
    Double hinhThucTrinhChieu;
    Double cachTrinhBay;
    Double noiDungYeuCau;
    Double tiepNhanThongTinCauHoi;
    Double traLoiPhanBien;
    Double traLoiHoiDong;
    Double sangTao;
    Double mucDoSuDung;
    Double trienVongDeTai;
    Double apDungCongNghe;

}

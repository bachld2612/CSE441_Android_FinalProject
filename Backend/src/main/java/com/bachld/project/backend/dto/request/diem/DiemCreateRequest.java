package com.bachld.project.backend.dto.request.diem;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiemCreateRequest {

    String maGV;
    Long maDeTai;
    String tenSV;
    String maSV;
    String tenDeTai;
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

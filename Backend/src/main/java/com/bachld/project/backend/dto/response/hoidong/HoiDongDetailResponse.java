package com.bachld.project.backend.dto.response.hoidong;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoiDongDetailResponse {

    Long id;
    String tenHoiDong;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    String loaiHoiDong;

    String chuTich;
    String thuKy;
    List<String> giangVienPhanBien;

    List<SinhVienTrongHoiDong> sinhVienList;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SinhVienTrongHoiDong {
        String hoTen;
        String maSV;
        String lop;
        String tenDeTai;
        String gvhd;
        String boMon;
    }
}

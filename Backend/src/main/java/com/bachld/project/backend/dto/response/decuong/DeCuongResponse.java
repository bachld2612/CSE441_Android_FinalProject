package com.bachld.project.backend.dto.response.decuong;

import com.bachld.project.backend.enums.DeCuongState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeCuongResponse {

    // Thuộc tính trực tiếp của DeCuong
    Long id;
    String deCuongUrl;
    DeCuongState trangThai;
    Integer soLanNop;
    String nhanXet;
    String tenDeTai;       // deTai.tenDeTai
    String maSV;           // deTai.sinhVienThucHien.maSV
    String hoTenSinhVien;  // deTai.sinhVienThucHien.hoTen
    String hoTenGiangVien; // deTai.gvhd.hoTen (GVHD)
}

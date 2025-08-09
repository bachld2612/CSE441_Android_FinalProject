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

    // Nếu entity DeCuong có auditing (created_at/updated_at) thì map được luôn
    LocalDate createdAt;
    LocalDate updatedAt;

    // Chỉ lấy các field yêu cầu từ quan hệ
    String tenDeTai;       // deTai.tenDeTai
    String maSV;           // deTai.sinhVienThucHien.maSV
    String hoTenSinhVien;  // deTai.sinhVienThucHien.hoTen
    String hoTenGiangVien; // deTai.gvhd.hoTen (GVHD)
}

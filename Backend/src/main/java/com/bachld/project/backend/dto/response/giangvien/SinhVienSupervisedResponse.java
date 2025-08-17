package com.bachld.project.backend.dto.response.giangvien;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVienSupervisedResponse {
    String maSV;
    String hoTen;
    String tenLop;
    String soDienThoai;
    String tenDeTai;
}
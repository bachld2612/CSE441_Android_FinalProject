package com.bachld.project.backend.dto.response.sinhvien;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVienCreationResponse {

    String maSV;
    String hoTen;
    String soDienThoai;
    String email;
    Long lopId;

}

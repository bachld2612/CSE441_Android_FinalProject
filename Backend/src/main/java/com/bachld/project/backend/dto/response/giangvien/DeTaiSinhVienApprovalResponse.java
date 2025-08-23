package com.bachld.project.backend.dto.response.giangvien;

import com.bachld.project.backend.enums.DeTaiState;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class DeTaiSinhVienApprovalResponse {
    String maSV;
    String hoTen;
    String tenLop;
    String soDienThoai;
    String tenDeTai;
    String idDeTai;
    DeTaiState trangThai;
    String tongQuanDeTaiUrl;
    String nhanXet;
}

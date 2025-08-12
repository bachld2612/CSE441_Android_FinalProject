package com.bachld.project.backend.dto.response.donhoandoan;

import com.bachld.project.backend.enums.HoanState;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonHoanDoAnResponse {
    Long id;

    Long sinhVienId;
    Long deTaiId;

    HoanState trangThai;
    String lyDo;
    String minhChungUrl;

    LocalDateTime requestedAt;
    LocalDateTime decidedAt;

    Long nguoiPheDuyetId;
    String ghiChuQuyetDinh;
}

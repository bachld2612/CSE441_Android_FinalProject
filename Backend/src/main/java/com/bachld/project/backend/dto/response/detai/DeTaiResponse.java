package com.bachld.project.backend.dto.response.detai;

import com.bachld.project.backend.enums.DeTaiState;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DeTaiResponse {
    Long id;
    String tenDeTai;
    DeTaiState trangThai;
    String nhanXet;

    Long gvhdId;
    String gvhdTen;
    Long sinhVienId;

    String tongQuanDeTaiUrl;
    String tongQuanFilename;
}
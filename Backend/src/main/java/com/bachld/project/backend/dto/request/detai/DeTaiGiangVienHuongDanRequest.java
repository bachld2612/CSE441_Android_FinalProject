package com.bachld.project.backend.dto.request.detai;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeTaiGiangVienHuongDanRequest {

    String maSV;
    String maGV;

}

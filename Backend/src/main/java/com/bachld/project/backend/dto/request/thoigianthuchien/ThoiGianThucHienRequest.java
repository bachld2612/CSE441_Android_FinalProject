package com.bachld.project.backend.dto.request.thoigianthuchien;

import com.bachld.project.backend.enums.CongViec;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThoiGianThucHienRequest {

    CongViec congViec;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    Long dotBaoVeId;

}


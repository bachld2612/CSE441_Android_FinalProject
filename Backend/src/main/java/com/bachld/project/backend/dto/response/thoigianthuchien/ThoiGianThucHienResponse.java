package com.bachld.project.backend.dto.response.thoigianthuchien;

import com.bachld.project.backend.enums.CongViec;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ThoiGianThucHienResponse {

    CongViec congViec;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    String tenDotBaoVe;

}

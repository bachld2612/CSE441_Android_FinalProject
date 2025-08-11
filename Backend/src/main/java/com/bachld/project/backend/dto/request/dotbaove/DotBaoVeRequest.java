package com.bachld.project.backend.dto.request.dotbaove;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DotBaoVeRequest {

    String tenDotBaoVe;
    int hocKi;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    int namBatDau;
    int namKetThuc;

}

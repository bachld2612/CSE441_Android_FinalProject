package com.bachld.project.backend.dto.response.hoidong;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoiDongListItemResponse {
    Long id;
    String tenHoiDong;
    LocalDate thoiGianBatDau;
    LocalDate thoiGianKetThuc;
    String loaiHoiDong;
}
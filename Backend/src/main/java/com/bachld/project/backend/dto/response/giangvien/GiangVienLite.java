package com.bachld.project.backend.dto.response.giangvien;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GiangVienLite {
    Long id;
    String maGV;
    String hoTen;
}
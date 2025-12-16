package com.bachld.project.backend.dto.request.diem;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiemCreateRequest {

    String maGV;
    Long maDeTai;
    String tenSV;
    String maSV;
    String tenDeTai;
    String nhanXetChung;
    Double Diem;

}

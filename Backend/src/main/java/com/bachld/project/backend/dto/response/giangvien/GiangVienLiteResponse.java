package com.bachld.project.backend.dto.response.giangvien;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiangVienLiteResponse {
    Long id;
    String hoTen;  // chỉ trả tên để render Select
}

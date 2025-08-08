package com.bachld.project.backend.dto.response.khoa;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhoaResponse {

    Long id;
    String tenKhoa;

}

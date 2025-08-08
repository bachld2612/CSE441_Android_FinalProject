package com.bachld.project.backend.dto.response.lop;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LopResponse {

    Long id;
    String tenLop;
    Long nganhId;

}

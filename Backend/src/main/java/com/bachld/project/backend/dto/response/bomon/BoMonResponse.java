package com.bachld.project.backend.dto.response.bomon;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoMonResponse {

    Long id;
    String tenBoMon;
    Long khoaId;

}

package com.bachld.project.backend.dto.request.bomon;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoMonRequest {

    @NotEmpty(message = "BO_MON_EMPTY")
    String tenBoMon;
    @NotEmpty(message = "KHOA_EMPTY")
    Long khoaId;

}

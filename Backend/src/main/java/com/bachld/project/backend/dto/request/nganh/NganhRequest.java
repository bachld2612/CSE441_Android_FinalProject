package com.bachld.project.backend.dto.request.nganh;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NganhRequest {

    @NotEmpty(message = "NGANH_EMPTY")
    String tenNganh;
    @NotEmpty(message = "KHOA_EMPTY")
    Long khoaId;

}

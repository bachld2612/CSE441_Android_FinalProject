package com.bachld.project.backend.dto.request.lop;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LopRequest {

    @NotEmpty(message = "LOP_EMPTY")
    String tenLop;
    @NotEmpty(message = "NGANH_EMPTY")
    Long nganhId;

}

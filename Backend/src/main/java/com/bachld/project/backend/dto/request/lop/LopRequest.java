package com.bachld.project.backend.dto.request.lop;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "NGANH_EMPTY")
    Long nganhId;

}

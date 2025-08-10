package com.bachld.project.backend.dto.request.detai;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeTaiApprovalRequest {
    @NotNull(message = "TRANG_THAI_INVALID")
    Boolean approved;   // true: PENDING → ACCEPTED, false: PENDING → CANCELED
    String nhanXet;
}

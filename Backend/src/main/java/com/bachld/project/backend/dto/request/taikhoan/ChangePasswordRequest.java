package com.bachld.project.backend.dto.request.taikhoan;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    @Size(min = 6, message = "PASSWORD_INVALID")
    String currentPassword;
    @Size(min = 6, message = "PASSWORD_INVALID")
    String newPassword;

}

package com.bachld.project.backend.dto.request.decuong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeCuongRequest {
    @NotEmpty(message = "DE_TAI_ID_EMPTY")
    private Long deTaiId;

    // Nếu bạn upload qua Cloudinary thì service sẽ set URL này sau khi upload
    @NotEmpty(message = "FILE_URL_EMPTY")
    private String fileUrl;
}

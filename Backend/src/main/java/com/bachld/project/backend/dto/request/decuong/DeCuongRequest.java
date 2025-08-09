package com.bachld.project.backend.dto.request.decuong;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeCuongRequest {
    @NotNull(message = "DE_TAI_ID_EMPTY")
    @Positive(message = "DE_TAI_ID_MUST_BE_POSITIVE")
    Long deTaiId;

    // Nếu bạn upload qua Cloudinary thì service sẽ set URL này sau khi upload
    @NotEmpty(message = "FILE_URL_EMPTY")
    String fileUrl;
}

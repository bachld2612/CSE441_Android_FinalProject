package com.bachld.project.backend.dto.request.decuong;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeCuongUploadRequest {
    @NotNull(message = "DE_TAI_ID_EMPTY")
    @Positive(message = "DE_TAI_ID_MUST_BE_POSITIVE")
    Long deTaiId;

    // Có thể nộp qua URL hoặc file
    String fileUrl;
    MultipartFile file;
}
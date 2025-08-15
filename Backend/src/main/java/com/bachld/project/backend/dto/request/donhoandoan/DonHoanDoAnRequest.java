package com.bachld.project.backend.dto.request.donhoandoan;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonHoanDoAnRequest {

    @NotBlank(message = "INVALID_VALIDATION")
    String lyDo;

    MultipartFile minhChungFile; // optional
}

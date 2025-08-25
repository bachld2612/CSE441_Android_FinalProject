package com.bachld.project.backend.dto.request.dotbaove;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddSinhVienToDotBaoVeRequest {

    MultipartFile dataFile;
    @NotNull(message = "NAM_BAT_DAU_EMPTY")
    int namBatDau;
    @NotNull(message = "NAM_KET_THUC_EMPTY")
    int namKetThuc;
    @NotNull(message = "HOC_KI_EMPTY")
    int hocKi;

}

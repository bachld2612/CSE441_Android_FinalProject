package com.bachld.project.backend.dto.request.thongbao;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThongBaoCreationRequest {

    String tieuDe;
    String noiDung;
    MultipartFile file;

}

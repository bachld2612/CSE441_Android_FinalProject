package com.bachld.project.backend.dto.request.giangvien;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TroLyKhoaCreationRequest {

    Long giangVienId;

}

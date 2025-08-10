package com.bachld.project.backend.dto.request.bomon;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TruongBoMonCreationRequest {

    Long giangVienId;
    Long boMonId;

}

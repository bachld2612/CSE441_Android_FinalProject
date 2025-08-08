package com.bachld.project.backend.dto.response.nganh;

import com.bachld.project.backend.entity.Khoa;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NganhResponse {

    String tenNganh;
    Long khoaId;

}

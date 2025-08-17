package com.bachld.project.backend.dto.response.nganh;

import com.bachld.project.backend.entity.Khoa;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NganhResponse {

    Long id;
    String tenNganh;
    Long khoaId;

}

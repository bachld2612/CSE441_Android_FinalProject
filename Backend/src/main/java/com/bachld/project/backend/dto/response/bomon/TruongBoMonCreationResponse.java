package com.bachld.project.backend.dto.response.bomon;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TruongBoMonCreationResponse {

    String maGV;
    String hoTen;
    String hocVi;
    String hocHam;
    String tenBoMon;

}

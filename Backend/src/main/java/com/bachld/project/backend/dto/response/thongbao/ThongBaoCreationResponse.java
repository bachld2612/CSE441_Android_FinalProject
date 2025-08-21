package com.bachld.project.backend.dto.response.thongbao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ThongBaoCreationResponse {

    Long id;
    String tieuDe;
    String noiDung;
    String fileUrl;
    LocalDate createdAt;

}

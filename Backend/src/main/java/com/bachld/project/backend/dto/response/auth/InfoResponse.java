package com.bachld.project.backend.dto.response.auth;

import com.bachld.project.backend.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoResponse {

    String maSV;
    String email;
    String maGV;
    String hoTen;
    String soDienThoai;
    String hocVi;
    String hocHam;
    String lop;
    String nganh;
    String boMon;
    String khoa;
    Role role;
    String anhDaiDienUrl;

}

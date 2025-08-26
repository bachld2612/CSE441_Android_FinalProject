package com.bachld.project.backend.dto.response.decuong;

import com.bachld.project.backend.enums.DeCuongState;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DeCuongLogResponse {
    String fileUrlMoiNhat;
    LocalDate ngayNopGanNhat;
    Integer tongSoLanNop;
    DeCuongState trangThaiHienTai;
    List<RejectNote> cacNhanXetTuChoi; // chỉ các lần bị từ chối trong quá khứ

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class RejectNote {
        LocalDate ngayNhanXet; // createdAt của log
        String lyDo;
    }
}

package com.bachld.project.backend.dto.response.hoidong;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddSinhVienToHoiDongResponse {
    int totalRecords;
    int successCount;
    int failureCount;
    List<FailureItem> failureItems;
    String logFileUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureItem {
        String maSinhVien;
        String tenDeTai;
        String reason;
    }
}

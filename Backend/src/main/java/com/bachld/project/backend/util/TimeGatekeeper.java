package com.bachld.project.backend.util;

import com.bachld.project.backend.entity.DotBaoVe;
import com.bachld.project.backend.entity.ThoiGianThucHien;
import com.bachld.project.backend.enums.CongViec;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.repository.DotBaoVeRepository;
import com.bachld.project.backend.repository.ThoiGianThucHienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class TimeGatekeeper {

    private final ThoiGianThucHienRepository thoiGianThucHienRepository;

    private static final ZoneId ZONE_BKK = ZoneId.of("Asia/Bangkok");
    private final DotBaoVeRepository dotBaoVeRepository;

    /**
     * Ném lỗi nếu hôm nay KHÔNG thuộc khoảng thời gian của công việc cv đối với đợt dot.
     * Trả về chính mốc thời gian để tái sử dụng nếu cần.
     */
    public ThoiGianThucHien assertWithinWindow(CongViec cv, DotBaoVe dot) {
        LocalDate today = LocalDate.now(ZONE_BKK);

        ThoiGianThucHien window = thoiGianThucHienRepository
                .findByDotBaoVe_IdAndCongViec(dot.getId(), cv)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW));

        if (today.isBefore(window.getThoiGianBatDau())) {
            throw new ApplicationException(ErrorCode.NO_ACTIVE_SUBMISSION_WINDOW);
        }
        if (today.isAfter(window.getThoiGianKetThuc())) {
            throw new ApplicationException(ErrorCode.OUT_OF_SUBMISSION_WINDOW);
        }
        return window;
    }

    public ThoiGianThucHien validateThoiGianDangKy(){
        LocalDate today = LocalDate.now(ZONE_BKK);
        return thoiGianThucHienRepository
                .findTopByCongViecAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqualOrderByThoiGianBatDauDesc(CongViec.DANG_KY_DE_TAI, today, today)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DANG_KY_TIME_INVALID));
    }

    public DotBaoVe getCurrentDotBaoVe(){
        LocalDate today = LocalDate.now(ZONE_BKK);
        return dotBaoVeRepository.
                findTopByThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqualOrderByThoiGianBatDauDesc(today, today)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_IN_DOT_BAO_VE));
    }
}

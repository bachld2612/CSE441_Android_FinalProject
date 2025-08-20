package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.response.hoidong.HoiDongDetailResponse;
import com.bachld.project.backend.dto.response.hoidong.HoiDongListItemResponse;
import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.enums.HoiDongType;
import com.bachld.project.backend.exception.ApplicationException;
import com.bachld.project.backend.exception.ErrorCode;
import com.bachld.project.backend.mapper.HoiDongMapper;
import com.bachld.project.backend.repository.HoiDongRepository;
import com.bachld.project.backend.service.HoiDongService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HoiDongServiceImpl implements HoiDongService {

    HoiDongRepository hoiDongRepository;
    HoiDongMapper hoiDongMapper;

    @PreAuthorize("isAuthenticated()")
    @Override
    public Page<HoiDongListItemResponse> getHoiDongsDangDienRa(String keyword, HoiDongType type, Pageable pageable) {
        LocalDate today = LocalDate.now();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        Page<HoiDong> page;
        if (type != null && hasKeyword) {
            page = hoiDongRepository.findByLoaiHoiDongAndTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
                            type, keyword, today, today, pageable);
        } else if (type != null) {
            page = hoiDongRepository
                    .findByLoaiHoiDongAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
                            type, today, today, pageable);
        } else if (hasKeyword) {
            page = hoiDongRepository
                    .findByTenHoiDongContainingIgnoreCaseAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
                            keyword, today, today, pageable);
        } else {
            page = hoiDongRepository
                    .findByThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
                            today, today, pageable);
        }

        return page.map(hoiDongMapper::toListItem);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public HoiDongDetailResponse getHoiDongDetail(Long hoiDongId) {
        HoiDong hd = hoiDongRepository.findById(hoiDongId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.HOI_DONG_NOT_FOUND));
        return hoiDongMapper.toDetail(hd);
    }
}

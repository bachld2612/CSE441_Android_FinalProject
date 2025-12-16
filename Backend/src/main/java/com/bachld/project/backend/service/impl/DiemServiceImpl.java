package com.bachld.project.backend.service.impl;

import com.bachld.project.backend.dto.request.diem.DiemCreateRequest;
import com.bachld.project.backend.entity.DeTai;
import com.bachld.project.backend.entity.Diem;
import com.bachld.project.backend.entity.GiangVien;
import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.repository.*;
import com.bachld.project.backend.service.DiemService;
import com.bachld.project.backend.util.Util;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class DiemServiceImpl implements DiemService {

    DiemRepository diemRepository;
    GiangVienRepository giangVienRepository;
    SinhVienRepository sinhVienRepository;
    DeTaiRepository deTaiRepository;
    HoiDongRepository hoiDongRepository;
    ThanhVienHoiDongRepository thanhVienHoiDongRepository;
    private final Util util;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void chamDiem(DiemCreateRequest request) {
        GiangVien giangVien = giangVienRepository.findByMaGV(request.getMaGV())
                .orElseThrow(() -> new IllegalArgumentException("Giảng viên không tồn tại"));

        DeTai deTai = deTaiRepository.findById(request.getMaDeTai())
                .orElseThrow(() -> new IllegalArgumentException("Đề tài không tồn tại"));

        if(Objects.equals(giangVien.getMaGV(), deTai.getGvhd().getMaGV())){
            throw new IllegalArgumentException("Giảng viên hướng dẫn không thể chấm điểm đề tài này");
        }

        Set<HoiDong> hoiDongSet = hoiDongRepository.findBydeTaiSet_Id(deTai.getId());

        Set<Long> hoiDongIds = hoiDongSet.stream().map(HoiDong::getId).collect(Collectors.toSet());

        List<HoiDong> hoiDongs = hoiDongRepository.findHoiDongsByMaGVAndIds(request.getMaGV(), hoiDongIds);

        if(hoiDongs.isEmpty()){
            throw new IllegalArgumentException("Giảng viên không nằm trong hội đồng của đề tài");
        }

        LocalDate today = LocalDate.now();

        boolean isValidDate = false;

        for(HoiDong hoiDong: hoiDongs){
            if (hoiDong.getThoiGianBatDau().isBefore(today) && hoiDong.getThoiGianKetThuc().isAfter(today)) {
                isValidDate = true;
                break;
            }
        }

        if(!isValidDate){
            throw new IllegalArgumentException("Đã hết thời gian chấm điểm");
        }

        Optional<Diem> diemOptional = diemRepository.findByMaGVAndMaDeTai(request.getMaGV(), request.getMaDeTai());
        Diem diem = new Diem();
        if (diemOptional.isPresent()) {
            diem = diemOptional.get();
        }

        if(request.getTenSV().trim().isEmpty()){
            throw new IllegalArgumentException("Tên sinh viên không được bỏ trống");
        }

        if (request.getTenSV().length() > 100){
            throw new IllegalArgumentException("Tên sinh viên không quá 100 ký tự");
        }

        if(util.chuaSoVaKiTuDacBietHoacKhoangTrangOCuoi(request.getTenSV())){
            throw new IllegalArgumentException("Tên sinh viên không chứa kí tự đặc biệt hoặc số hoặc khoảng trắng ở cuối.");
        }

        if(request.getMaSV().trim().isEmpty()){
            throw new IllegalArgumentException("Mã sinh viên không được bỏ trống");
        }

        if (request.getTenSV().length() > 20){
            throw new IllegalArgumentException("Mã sinh viên không quá 20 ký tự");
        }

        if(util.chuaSoVaKiTuDacBietHoacKhoangTrangOCuoi(request.getMaGV())){
            throw new IllegalArgumentException("Mã sinh viên không chứa kí tự đặc biệt");
        }

        if(request.getTenDeTai().trim().isEmpty()){
            throw new IllegalArgumentException("Tên đề tài không được bỏ trống");
        }

        if (request.getTenDeTai().length() > 500){
            throw new IllegalArgumentException("Tên đề tài không quá 500 ký tự");
        }

        if(util.chuaSo(request.getTenDeTai())){
            throw new IllegalArgumentException("Tên đề tài không chứa số");
        }

        if(!request.getNhanXetChung().trim().isEmpty() && util.chuaSoVaKiTuDacBietHoacKhoangTrangOCuoi(request.getNhanXetChung())){
            throw new IllegalArgumentException("Nhận xét chung không được chứa số hoặc ký tự đặc biệt");
        }

        if(request.getDiem() == null){
            throw new IllegalArgumentException("Điểm không được bỏ trống");
        }

        if(request.getDiem() < 0 || request.getDiem() > 10.0){
            throw new IllegalArgumentException("Điểm chỉ được phép từ 0 đến 10.0");
        }

        if(!util.kiemTraThapPhan(request.getDiem())){
            throw new IllegalArgumentException("Điểm không được quá 3 chữ số thập phân");
        }

        diem.setMaGV(request.getMaGV());
        diem.setMaDeTai(request.getMaDeTai());
        diem.setTenSV(request.getTenSV());
        diem.setMaSV(request.getMaSV());
        diem.setTenDeTai(request.getTenDeTai());
        diem.setNhanXetChung(request.getNhanXetChung());
        diem.setDiem(request.getDiem());
      
        diemRepository.save(diem);
    }
}

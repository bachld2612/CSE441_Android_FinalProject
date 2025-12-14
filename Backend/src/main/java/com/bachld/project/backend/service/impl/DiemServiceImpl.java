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

        if(request.getTinhThucTien() == null){
            throw new IllegalArgumentException("Điểm thực tiễn không được bỏ trống");
        }

        if(request.getTinhThucTien() < 0 || request.getTinhThucTien() > 0.5){
            throw new IllegalArgumentException("Điểm thực tiễn chỉ được phép từ 0 đến 0.5");
        }

        if(!util.kiemTraThapPhan(request.getTinhThucTien())){
            throw new IllegalArgumentException("Điểm thực tiễn không được quá 3 chữ số thập phân");
        }

        if (request.getThoiGianTrinhBay() == null) {
            throw new IllegalArgumentException("Điểm thời gian trình bày không được bỏ trống");
        }

        if (request.getThoiGianTrinhBay() < 0 || request.getThoiGianTrinhBay() > 0.2) {
            throw new IllegalArgumentException("Điểm thời gian trình bày chỉ được phép từ 0 đến 0.2");
        }

        if(!util.kiemTraThapPhan(request.getThoiGianTrinhBay())){
            throw new IllegalArgumentException("Điểm thời gian trình bày không được quá 3 chữ số thập phân");
        }

        if (request.getHinhVeSanPham() == null) {
            throw new IllegalArgumentException("Điểm hình vẽ sản phẩm không được bỏ trống");
        }

        if (request.getHinhVeSanPham() < 0 || request.getHinhVeSanPham() > 0.3) {
            throw new IllegalArgumentException("Điểm hình vẽ sản phẩm chỉ được phép từ 0 đến 0.3");
        }

        if(!util.kiemTraThapPhan(request.getHinhVeSanPham())){
            throw new IllegalArgumentException("Điểm hình vẽ sản phẩm không được quá 3 chữ số thập phân");
        }

        if (request.getHinhThucTrinhChieu() == null) {
            throw new IllegalArgumentException("Điểm hình thức trình chiếu không được bỏ trống");
        }

        if (request.getHinhThucTrinhChieu() < 0 || request.getHinhThucTrinhChieu() > 1.0) {
            throw new IllegalArgumentException("Điểm hình thức trình chiếu chỉ được phép từ 0 đến 1.0");
        }

        if(!util.kiemTraThapPhan(request.getHinhThucTrinhChieu())){
            throw new IllegalArgumentException("Điểm hình thức trình chiếu không được quá 3 chữ số thập phân");
        }

        if (request.getCachTrinhBay() == null) {
            throw new IllegalArgumentException("Điểm cách trình bày không được bỏ trống");
        }

        if (request.getCachTrinhBay() < 0 || request.getCachTrinhBay() > 2.0) {
            throw new IllegalArgumentException("Điểm cách trình bày chỉ được phép từ 0 đến 2.0");
        }

        if(!util.kiemTraThapPhan(request.getCachTrinhBay())){
            throw new IllegalArgumentException("Điểm cách trình bày không được quá 3 chữ số thập phân");
        }

        if (request.getNoiDungYeuCau() == null) {
            throw new IllegalArgumentException("Điểm nội dung yêu cầu không được bỏ trống");
        }

        if (request.getNoiDungYeuCau() < 0 || request.getNoiDungYeuCau() > 1.0) {
            throw new IllegalArgumentException("Điểm nội dung yêu cầu chỉ được phép từ 0 đến 1.0");
        }

        if(!util.kiemTraThapPhan(request.getNoiDungYeuCau())){
            throw new IllegalArgumentException("Điểm nội dung yêu cầu không được quá 3 chữ số thập phân");
        }

        if (request.getTiepNhanThongTinCauHoi() == null) {
            throw new IllegalArgumentException("Điểm tiếp nhận thông tin câu hỏi không được bỏ trống");
        }

        if (request.getTiepNhanThongTinCauHoi() < 0 || request.getTiepNhanThongTinCauHoi() > 0.5) {
            throw new IllegalArgumentException("Điểm tiếp nhận thông tin câu hỏi chỉ được phép từ 0 đến 0.5");
        }

        if(!util.kiemTraThapPhan(request.getTiepNhanThongTinCauHoi())){
            throw new IllegalArgumentException("Điểm tiếp nhận thông tin câu hỏi không được quá 3 chữ số thập phân");
        }

        if (request.getTraLoiPhanBien() == null) {
            throw new IllegalArgumentException("Điểm trả lời phản biện không được bỏ trống");
        }

        if (request.getTraLoiPhanBien() < 0 || request.getTraLoiPhanBien() > 2.0) {
            throw new IllegalArgumentException("Điểm trả lời phản biện chỉ được phép từ 0 đến 2.0");
        }

        if(!util.kiemTraThapPhan(request.getTraLoiPhanBien())){
            throw new IllegalArgumentException("Điểm trả lời phản biện không được quá 3 chữ số thập phân");
        }

        if (request.getTraLoiHoiDong() == null) {
            throw new IllegalArgumentException("Điểm trả lời hội đồng không được bỏ trống");
        }

        if (request.getTraLoiHoiDong() < 0 || request.getTraLoiHoiDong() > 1.5) {
            throw new IllegalArgumentException("Điểm trả lời hội đồng chỉ được phép từ 0 đến 1.5");
        }

        if(!util.kiemTraThapPhan(request.getTraLoiHoiDong())){
            throw new IllegalArgumentException("Điểm trả lời hội đồng không được quá 3 chữ số thập phân");
        }

        if (request.getSangTao() == null) {
            throw new IllegalArgumentException("Điểm sáng tạo không được bỏ trống");
        }

        if (request.getSangTao() < 0 || request.getSangTao() > 0.2) {
            throw new IllegalArgumentException("Điểm sáng tạo chỉ được phép từ 0 đến 0.2");
        }

        if(!util.kiemTraThapPhan(request.getSangTao())){
            throw new IllegalArgumentException("Điểm sáng tạo không được quá 3 chữ số thập phân");
        }

        if (request.getMucDoSuDung() == null) {
            throw new IllegalArgumentException("Điểm mức độ sử dụng không được bỏ trống");
        }

        if (request.getMucDoSuDung() < 0 || request.getMucDoSuDung() > 0.2) {
            throw new IllegalArgumentException("Điểm mức độ sử dụng chỉ được phép từ 0 đến 0.2");
        }

        if(!util.kiemTraThapPhan(request.getMucDoSuDung())){
            throw new IllegalArgumentException("Điểm mức độ sử dụng không được quá 3 chữ số thập phân");
        }

        if (request.getTrienVongDeTai() == null) {
            throw new IllegalArgumentException("Điểm triển vọng đề tài không được bỏ trống");
        }

        if (request.getTrienVongDeTai() < 0 || request.getTrienVongDeTai() > 0.2) {
            throw new IllegalArgumentException("Điểm triển vọng đề tài chỉ được phép từ 0 đến 0.2");
        }

        if(!util.kiemTraThapPhan(request.getTrienVongDeTai())){
            throw new IllegalArgumentException("Điểm triển vọng đề tài không được quá 3 chữ số thập phân");
        }

        if (request.getApDungCongNghe() == null) {
            throw new IllegalArgumentException("Điểm áp dụng công nghệ không được bỏ trống");
        }

        if (request.getApDungCongNghe() < 0 || request.getApDungCongNghe() > 0.4) {
            throw new IllegalArgumentException("Điểm áp dụng công nghệ chỉ được phép từ 0 đến 0.4");
        }

        if(!util.kiemTraThapPhan(request.getApDungCongNghe())){
            throw new IllegalArgumentException("Điểm áp dụng công nghệ không được quá 3 chữ số thập phân");
        }

        diem.setMaGV(request.getMaGV());
        diem.setMaDeTai(request.getMaDeTai());
        diem.setTenSV(request.getTenSV());
        diem.setMaSV(request.getMaSV());
        diem.setTenDeTai(request.getTenDeTai());
        diem.setNhanXetChung(request.getNhanXetChung());

        diem.setTinhThucTien(request.getTinhThucTien());
        diem.setThoiGianTrinhBay(request.getThoiGianTrinhBay());
        diem.setHinhVeSanPham(request.getHinhVeSanPham());
        diem.setHinhThucTrinhChieu(request.getHinhThucTrinhChieu());
        diem.setCachTrinhBay(request.getCachTrinhBay());
        diem.setNoiDungYeuCau(request.getNoiDungYeuCau());
        diem.setTiepNhanThongTinCauHoi(request.getTiepNhanThongTinCauHoi());
        diem.setTraLoiPhanBien(request.getTraLoiPhanBien());
        diem.setTraLoiHoiDong(request.getTraLoiHoiDong());
        diem.setSangTao(request.getSangTao());
        diem.setMucDoSuDung(request.getMucDoSuDung());
        diem.setTrienVongDeTai(request.getTrienVongDeTai());
        diem.setApDungCongNghe(request.getApDungCongNghe());

        diemRepository.save(diem);
    }
}

import java.time.LocalDate;

import com.bachld.project.backend.dto.request.diem.DiemCreateRequest;
import com.bachld.project.backend.entity.Diem;
import com.bachld.project.backend.entity.HoiDong;
import com.bachld.project.backend.entity.ThanhVienHoiDong;
import com.bachld.project.backend.enums.DeTaiState;

@Transactional
 1  public void chamDiem(DiemCreateRequest request) {
 2
 3      // 1. Mỗi GV chỉ được chấm 1 lần / đề tài
 4      if (diemRepository.findByMaGVAndMaDeTai(gv.getMaGV(), deTai.getId()).isPresent()) {
 5          throw new IllegalArgumentException("Không được chấm nhiều hơn 1 lần cho đề tài này");
 6      }
 7
 8      // 2. Một sinh viên tối đa 5 lần chấm
 9      if (diemRepository.countByMaSV(request.getMaSV()) >= 5) {
10          throw new IllegalArgumentException("Mỗi sinh viên chỉ được chấm tối đa 5 lần");
11      }
12
13      // 3. Giảng viên chỉ có thể chấm điểm các đề tài mà họ tham gia trong hội đồng
14      boolean thuocHoiDong = false;
15      for (HoiDong hoiDong : hoiDongs) {
16          for (ThanhVienHoiDong thanhVien : hoiDong.getThanhVienHoiDongSet()) {
17              if (thanhVien.getGiangVien().getMaGV().equals(gv.getMaGV())) {
18                  thuocHoiDong = true;
19                  break;
20              }
21          }
22          if (thuocHoiDong) break;
23      }
24
25      if (!thuocHoiDong) {
26          throw new IllegalArgumentException("Giảng viên không nằm trong hội đồng của đề tài");
27      }
28
29      LocalDate today = LocalDate.now();
30      boolean isValidDate = false;
31
32      // 4. Kiểm tra thời gian chấm điểm
33      for (HoiDong hoiDong : hoiDongs) {
34          if (!today.isBefore(hoiDong.getThoiGianBatDau())
35              && !today.isAfter(hoiDong.getThoiGianKetThuc())) {
36              isValidDate = true;
37              break;
38          }
39      }
40
41      if (!isValidDate) {
42          throw new IllegalArgumentException("Đã hết thời gian chấm điểm");
43      }
44
45      // 5. Sinh viên phải có đồ án được phê duyệt
46      if (deTai.getTrangThai() != DeTaiState.DA_PHE_DUYET) {
47          throw new IllegalArgumentException("Đề tài chưa được phê duyệt để chấm điểm");
48      }
49
50      // ===== VALIDATE INPUT =====
51
52      // 6. Mã sinh viên không được bỏ trống
53      if (request.getMaSV() == null || request.getMaSV().trim().isEmpty()) {
54          throw new IllegalArgumentException("Mã sinh viên không được bỏ trống");
55      }
56
57      // 7. Mã sinh viên không vượt quá 20 ký tự
58      if (request.getMaSV().length() > 20) {
59          throw new IllegalArgumentException("Mã sinh viên không được vượt quá 20 ký tự");
60      }
61
62      // 8. Mã sinh viên chỉ được chứa ký tự số
63      if (!request.getMaSV().matches("\\d+")) {
64          throw new IllegalArgumentException("Mã sinh viên chỉ được chứa ký tự số");
65      }
66
67      // 9. Nhận xét không chứa số hoặc ký tự đặc biệt
68      if (request.getNhanXetChung() != null &&
69          util.chuaSoVaKiTuDacBietHoacKhoangTrangOCuoi(request.getNhanXetChung())) {
70          throw new IllegalArgumentException("Nhận xét không được chứa số hoặc ký tự đặc biệt");
71      }
72
73      // 10. Điểm không được bỏ trống
74      Double diem = request.getDiem();
75      if (diem == null) {
76          throw new IllegalArgumentException("Điểm không được bỏ trống");
77      }
78
79      // 11. Điểm nằm trong khoảng 0 – 10
80      if (diem < 0 || diem > 10.0) {
81          throw new IllegalArgumentException("Điểm phải nằm trong khoảng 0 đến 10.0");
82      }
83
84      // 12. Điểm chỉ có tối đa 1 chữ số thập phân
85      if (Math.round(diem * 10) != diem * 10) {
86          throw new IllegalArgumentException("Điểm không được quá 1 chữ số thập phân");
87      }
88
89      // ===== LƯU ĐIỂM =====
90      Diem d = new Diem();
91      d.setMaGV(gv.getMaGV());
92      d.setMaDeTai(deTai.getId());
93      d.setMaSV(request.getMaSV());
94      d.setDiem(diem);
95      d.setNhanXetChung(request.getNhanXetChung());
96
97      diemRepository.save(d);
98  }



 1  public Double tinhDiemCuoi(Long maDeTai, String maSV) {
 2
 3      List<Diem> dsDiem = diemRepository.findByMaSVAndMaDeTai(maSV, maDeTai);
 4      if (dsDiem == null || dsDiem.isEmpty()) {
 5          return null;
 6      }
 7
 8      // 1. Tính điểm trung bình ban đầu
 9      double tongDiem = 0;
10      for (int i = 0; i < dsDiem.size(); i++) {
11          tongDiem += dsDiem.get(i).getDiem();
12      }
13      double diemTrungBinh = tongDiem / dsDiem.size();
14
15      // 2. Loại các điểm lệch quá 1.5 so với trung bình
16      double tongDiemHopLe = 0;
17      int soLuongHopLe = 0;
18
19      for (int i = 0; i < dsDiem.size(); i++) {
20          double diem = dsDiem.get(i).getDiem();
21          if (Math.abs(diem - diemTrungBinh) <= 1.5) {
22              tongDiemHopLe += diem;
23              soLuongHopLe++;
24          }
25      }
26
27      if (soLuongHopLe == 0) {
28          return null;
29      }
30
31      // 3. Tính điểm cuối
32      double diemCuoi = tongDiemHopLe / soLuongHopLe;
33      return diemCuoi;
34
35  }


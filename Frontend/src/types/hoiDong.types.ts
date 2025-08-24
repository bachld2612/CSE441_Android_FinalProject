export type HoiDongType = "PEER_REVIEW" | "DEFENSE";

export interface HoiDongListItem {
  id: number;
  tenHoiDong: string;
  thoiGianBatDau: string;
  thoiGianKetThuc: string;
  loaiHoiDong: HoiDongType;
}

export interface HoiDongCreateRequest {
  tenHoiDong: string;
  thoiGianBatDau: string; 
  thoiGianKetThuc: string;
  loaiHoiDong: HoiDongType;
}

export interface HoiDongDetail {
  id: number;
  tenHoiDong: string;
  thoiGianBatDau: string;
  thoiGianKetThuc: string;
  loaiHoiDong: HoiDongType;
  chuTich?: string | null;
  thuKy?: string | null;
  giangVienPhanBien?: string[] | null;
  sinhVienList?: Array<{
    hoTen: string;
    maSV: string;
    lop?: string;
    tenDeTai?: string;
    gvhd?: string;
    boMon?: string;
  }> | null;
}

export interface ImportResult {
  totalRecords: number;
  successCount: number;
  failureCount: number;
  logFileUrl?: string;
  failureItems?: Array<{
    maSinhVien: string;
    tenDeTai?: string;
    reason: string;
  }>;
}

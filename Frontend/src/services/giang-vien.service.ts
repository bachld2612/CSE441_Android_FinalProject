// src/services/giang-vien.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

/** Generic Page */
export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

/** DTOs chính */
export interface GiangVienResponse {
  id: number;
  maGV: string;
  hoTen: string;
  soDienThoai?: string;
  hocVi?: string;
  hocHam?: string;
  email?: string;
  boMonId?: number;
}

/** ====== GIỮ NGUYÊN PHẦN CŨ ====== */
export interface GiangVienLite {
  id: number;
  hoTen: string;
}
export interface GiangVienCreationRequest {
  maGV: string;
  hoTen: string;
  soDienThoai: string;
  email: string;
  matKhau: string;
  hocVi?: string;
  hocHam?: string;
  boMonId: number;
}
export interface GiangVienCreationResponse {
  maGV: string;
  hoTen: string;
  soDienThoai?: string;
  hocVi?: string;
  hocHam?: string;
  email?: string;
  boMonId?: number;
}
export interface TroLyKhoaCreationRequest {
  giangVienId: number;
}
export interface GiangVienImportResponse {
  totalRows: number;
  success: number;
  errors: string[];
}

export async function getGiangVienByBoMon(
  boMonId: number
): Promise<ApiResponse<GiangVienLite[]>> {
  const res: ApiResponse<GiangVienLite[]> = await api.get(
    `/giang-vien/by-bo-mon/${boMonId}`
  );
  return res;
}
export async function createGiangVien(
  data: GiangVienCreationRequest
): Promise<ApiResponse<GiangVienCreationResponse>> {
  const res: ApiResponse<GiangVienCreationResponse> = await api.post(
    "/giang-vien",
    data
  );
  return res;
}
export async function createTroLyKhoa(
  data: TroLyKhoaCreationRequest
): Promise<ApiResponse<string>> {
  const res: ApiResponse<string> = await api.post(
    "/giang-vien/tro-ly-khoa",
    data
  );
  return res;
}
export async function importGiangVien(
  file: File
): Promise<ApiResponse<GiangVienImportResponse>> {
  const formData = new FormData();
  formData.append("file", file);
  const res: ApiResponse<GiangVienImportResponse> = await api.post(
    "/giang-vien/import",
    formData,
    { headers: { "Content-Type": "multipart/form-data" } }
  );
  return res;
}

/** ====== SỬA ĐƯỜNG DẪN (mới) ====== */
// GET /api/v1/giang-vien/list
export async function getGiangVienPage(params?: {
  page?: number;
  size?: number;
  sort?: string; // "maGV,asc" | "hoTen,desc"
}): Promise<Page<GiangVienResponse>> {
  const res: ApiResponse<Page<GiangVienResponse>> = await api.get(
    "/giang-vien/list",
    {
      params: {
        page: params?.page ?? 0,
        size: params?.size ?? 10,
        sort: params?.sort ?? "maGV,asc",
      },
    }
  );
  return res.result!;
}

/** ====== (tuỳ chọn) BỔ SUNG 2 HÀM PHÙ HỢP CONTROLLER MỚI ====== */

// GET /api/v1/giang-vien/sinh-vien
export interface SinhVienSupervisedResponse {
  maSV: string;
  hoTen: string;
  tenLop?: string;
  soDienThoai?: string;
  tenDeTai?: string;
  // ... nếu backend trả thêm, bổ sung ở đây
}
export async function getMySupervisedStudents(params?: {
  page?: number;
  size?: number;
  sort?: string; // "hoTen,asc" ...
}): Promise<Page<SinhVienSupervisedResponse>> {
  const res: ApiResponse<Page<SinhVienSupervisedResponse>> = await api.get(
    "/giang-vien/sinh-vien",
    {
      params: {
        page: params?.page ?? 0,
        size: params?.size ?? 10,
        sort: params?.sort ?? "hoTen,asc",
      },
    }
  );
  return res.result!;
}

// GET /api/v1/giang-vien/xet-duyet/sinh-vien?status=ACCEPTED
export type DeTaiState = "ACCEPTED" | "REJECTED" | "PENDING" | string; // nới lỏng cho an toàn
export interface DeTaiSinhVienApprovalResponse {
  maSV: string;
  hoTen: string;
  tenDeTai: string;
  // có thể có thêm: ngayDangKy, ghiChu, ...
}
export async function getDeTaiSinhVienApproval(
  status: DeTaiState = "ACCEPTED",
  params?: { page?: number; size?: number; sort?: string }
): Promise<Page<DeTaiSinhVienApprovalResponse>> {
  const res: ApiResponse<Page<DeTaiSinhVienApprovalResponse>> = await api.get(
    "/giang-vien/xet-duyet/sinh-vien",
    {
      params: {
        status,
        page: params?.page ?? 0,
        size: params?.size ?? 10,
        sort: params?.sort ?? "hoTen,asc",
      },
    }
  );
  return res.result!;
}

/** Export gọn */
export default {
  getGiangVienByBoMon,
  createGiangVien,
  importGiangVien,
  createTroLyKhoa,
  getGiangVienPage,
  // optional
  getMySupervisedStudents,
  getDeTaiSinhVienApproval,
};

export {
  type GiangVienLite as TGiangVienLite,
  type GiangVienResponse as TGiangVienResponse,
  type Page as TPage,
  type SinhVienSupervisedResponse as TSinhVienSupervisedResponse,
  type DeTaiSinhVienApprovalResponse as TDeTaiSinhVienApprovalResponse,
};

import api from "@/lib/axios";
import type {
  ApiResponse,
  PageResponse,
  PageableRequest,
} from "@/types/api-response";

export interface DeTai {
  maSV: string;
  hoTen: string;
  tenLop: string;
  soDienThoai?: string;
  tenDeTai: string;
  trangThai: "PENDING" | "ACCEPTED" | "REJECTED";
  tongQuanDeTaiUrl?: string;
}

export interface DeTaiGiangVienHuongDanResponse {
  success: boolean;
}

export interface DeTaiGiangVienHuongDanRequest {
  maSV: string;
  maGV: string;
}

const getDeTaiApproval = async (
  pageable: PageableRequest
): Promise<PageResponse<DeTai>> => {
  const res: ApiResponse<PageResponse<DeTai>> = await api.get(
    "/giang-vien/do-an/xet-duyet-de-tai",
    { params: pageable }
  );
  return res.result!;
};

const approveDeTai = async (maSV: string): Promise<ApiResponse<string>> => {
  return await api.post(`/giang-vien/do-an/xet-duyet-de-tai/${maSV}/approve`);
};

const rejectDeTai = async (maSV: string): Promise<ApiResponse<string>> => {
  return await api.post(`/giang-vien/do-an/xet-duyet-de-tai/${maSV}/reject`);
};

const addGiangVienHuongDan = async (
  data: DeTaiGiangVienHuongDanRequest
): Promise<ApiResponse<DeTaiGiangVienHuongDanResponse>> => {
  try {
    const res: ApiResponse<DeTaiGiangVienHuongDanResponse> = await api.post(
      "/de-tai/gan-de-tai",
      data
    );
    return res;
  } catch (error) {
    console.error(
      "DeTaiService - addGiangVienHuongDan error:",
      error
    );
    throw error; 
  }
};

export { getDeTaiApproval, approveDeTai, rejectDeTai, addGiangVienHuongDan };

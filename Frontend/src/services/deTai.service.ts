import api from "@/lib/axios";
import type {
  ApiResponse,
  PageResponse,
  PageableRequest,
} from "@/types/apiResponse";

export interface DeTai {
  maSV: string;
  hoTen: string;
  tenLop: string;
  soDienThoai?: string;
  tenDeTai: string;
  trangThai: "PENDING" | "ACCEPTED" | "REJECTED";
  tongQuanDeTaiUrl?: string;
  tongQuanFilename?: string;
  idDeTai: string;
  nhanXet?: string;
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

async function approveDeTai(idDeTai: string, nhanXet: string) {
  const res = await api.put(
    `/giang-vien/do-an/xet-duyet-de-tai/${idDeTai}/approve`,
    { nhanXet }
  );
  return res.data;
}

async function rejectDeTai(idDeTai: string, nhanXet: string) {
  const res = await api.put(
    `/giang-vien/do-an/xet-duyet-de-tai/${idDeTai}/reject`,
    { nhanXet }
  );
  return res.data;
}

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
    console.error("DeTaiService - addGiangVienHuongDan error:", error);
    throw error;
  }
};

export { getDeTaiApproval, approveDeTai, rejectDeTai, addGiangVienHuongDan };

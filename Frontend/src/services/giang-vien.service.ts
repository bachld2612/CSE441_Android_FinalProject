// src/services/giang-vien.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

export interface GiangVienLite {
  id: number;
  maGV: string;
  hoTen: string;
}

export interface GiangVienInfoResponse {
  maGV?: string;
  hoTen?: string;
  hocVi?: string;
  hocHam?: string;
  soLuongDeTai?: number;
}

// Mặc định gọi theo REST dưới /bo-mon/{id}/giang-vien.
// Nếu BE là kiểu query (/giang-vien?boMonId=...), chỉ cần đổi URL/params.
async function getGiangVienByBoMon(
  boMonId: number
): Promise<ApiResponse<GiangVienLite[]>> {
  const res: ApiResponse<GiangVienLite[]> = await api.get(
    `/bo-mon/${boMonId}/giang-vien`
  );
  return res;
}

const getGiangVienByBoMonAndSoLuongDeTai = async (
  boMonId: number
): Promise<ApiResponse<GiangVienInfoResponse[]>> => {
  try {
    const res: ApiResponse<GiangVienInfoResponse[]> = await api.get(
      `/giang-vien/${boMonId}`
    );
    return res;
  } catch (error) {
    console.error(
      "GiangVienService - getGiangVienByBoMonAndSoLuongDeTai error:",
      error
    );
    throw error; // Ném lỗi để xử lý ở nơi gọi
  }
};

export { getGiangVienByBoMon, getGiangVienByBoMonAndSoLuongDeTai };

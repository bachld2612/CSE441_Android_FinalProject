// src/services/giang-vien.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

export interface GiangVienLite {
  id: number;
  maGV: string;
  hoTen: string;
}

// Mặc định gọi theo REST dưới /bo-mon/{id}/giang-vien.
// Nếu BE là kiểu query (/giang-vien?boMonId=...), chỉ cần đổi URL/params.
export async function getGiangVienByBoMon(
  boMonId: number
): Promise<ApiResponse<GiangVienLite[]>> {
  const res: ApiResponse<GiangVienLite[]> = await api.get(
    `/bo-mon/${boMonId}/giang-vien`
  );
  return res;
}

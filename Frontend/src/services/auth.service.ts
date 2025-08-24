// src/services/auth.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

// ===== DTO từ backend =====
interface InfoResponse {
  maSV?: string;
  email?: string;
  maGV?: string;
  hoTen?: string;
  soDienThoai?: string;
  hocVi?: string;
  hocHam?: string;
  lop?: string;
  nganh?: string;
  boMon?: string;
  khoa?: string;
  role?: "ADMIN" | "GIANG_VIEN" | "TRUONG_BO_MON" | "SINH_VIEN" | string;
  anhDaiDienUrl?: string;
}

// ===== Service =====
async function getMyInfo(): Promise<ApiResponse<InfoResponse>> {
  const res: ApiResponse<InfoResponse> = await api.get("/auth/my-info");
  return res;
}

export { getMyInfo, type InfoResponse };

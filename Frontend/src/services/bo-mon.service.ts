// src/services/bo-mon.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

// ====================
// DTO mapping từ backend
// ====================
interface BoMonRequest {
  tenBoMon: string;
  khoaId: number;
}

interface BoMonResponse {
  id: number;
  tenBoMon: string;
  khoaId: number;
}

// (Backend GET /bo-mon trả Page<BoMonResponse>, ta bóc content -> mảng)
type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

// ====================
// Service functions
// (axios.ts đã return response.data, nên ở đây return thẳng `res`)
// ====================

// Lấy tất cả bộ môn dưới dạng mảng (đồng nhất với Khoa)
async function getAllBoMon(): Promise<ApiResponse<BoMonResponse[]>> {
  const res: ApiResponse<Page<BoMonResponse>> = await api.get("/bo-mon", {
    params: { page: 0, size: 1000, sort: "updatedAt,DESC" },
  });

  return {
    code: res.code,
    message: res.message,
    result: res.result?.content ?? [],
  };
}

async function createBoMon(
  data: BoMonRequest
): Promise<ApiResponse<BoMonResponse>> {
  const res: ApiResponse<BoMonResponse> = await api.post("/bo-mon", data);
  return res;
}

async function updateBoMon(
  id: number,
  data: BoMonRequest
): Promise<ApiResponse<BoMonResponse>> {
  const res: ApiResponse<BoMonResponse> = await api.put(`/bo-mon/${id}`, data);
  return res;
}

async function deleteBoMon(id: number): Promise<ApiResponse<string>> {
  const res: ApiResponse<string> = await api.delete(`/bo-mon/${id}`);
  return res;
}

// ====================
// Export gọn
// ====================
export {
  getAllBoMon,
  createBoMon,
  updateBoMon,
  deleteBoMon,
  type BoMonRequest,
  type BoMonResponse,
};

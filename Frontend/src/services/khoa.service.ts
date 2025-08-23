import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

// ====================
// DTO mapping từ backend
// ====================
interface KhoaRequest {
  tenKhoa: string;
}

interface KhoaResponse {
  id: number;
  tenKhoa: string;
}

// ====================
// Service functions
// (axios.ts đã return response.data, nên ở đây return thẳng `res`)
// ====================
async function getAllKhoa(): Promise<ApiResponse<KhoaResponse[]>> {
  const res: ApiResponse<KhoaResponse[]> = await api.get("/khoa");
  return res;
}

async function createKhoa(
  data: KhoaRequest
): Promise<ApiResponse<KhoaResponse>> {
  const res: ApiResponse<KhoaResponse> = await api.post("/khoa", data);
  return res;
}

async function updateKhoa(
  id: number,
  data: KhoaRequest
): Promise<ApiResponse<KhoaResponse>> {
  const res: ApiResponse<KhoaResponse> = await api.put(`/khoa/${id}`, data);
  return res;
}

async function deleteKhoa(id: number): Promise<ApiResponse<string>> {
  const res: ApiResponse<string> = await api.delete(`/khoa/${id}`);
  return res;
}

// ====================
// Export gọn
// ====================
export {
  getAllKhoa,
  createKhoa,
  updateKhoa,
  deleteKhoa,
  type KhoaRequest,
  type KhoaResponse,
};

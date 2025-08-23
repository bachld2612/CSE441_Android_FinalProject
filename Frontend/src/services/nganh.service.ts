// src/services/nganh.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

// ===== DTO từ backend =====
interface NganhRequest {
  tenNganh: string;
  khoaId: number;
}

interface NganhResponse {
  id: number;
  tenNganh: string | null;
  khoaId: number | null;
}

// Spring Data Page
interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first?: boolean;
  last?: boolean;
  empty?: boolean;
}

// Lấy Page (dùng khi cần phân trang)
async function getNganhPage(params?: {
  page?: number;
  size?: number;
  sort?: string;
}): Promise<ApiResponse<Page<NganhResponse>>> {
  const res: ApiResponse<Page<NganhResponse>> = await api.get("/nganh", {
    params: {
      page: params?.page ?? 0,
      size: params?.size ?? 1000,
      sort: params?.sort ?? "updatedAt,DESC",
    },
  });
  return res;
}

// Lấy tất cả: flatten Page -> array để dùng giống Khoa/Bộ môn
async function getAllNganh(): Promise<ApiResponse<NganhResponse[]>> {
  const pageRes = await getNganhPage({ page: 0, size: 1000, sort: "updatedAt,DESC" });
  return {
    code: pageRes.code,
    message: pageRes.message,
    result: pageRes.result?.content ?? [],
  };
}

async function createNganh(
  data: NganhRequest
): Promise<ApiResponse<NganhResponse>> {
  const res: ApiResponse<NganhResponse> = await api.post("/nganh", data);
  return res;
}

async function updateNganh(
  id: number,
  data: NganhRequest
): Promise<ApiResponse<NganhResponse>> {
  const res: ApiResponse<NganhResponse> = await api.put(`/nganh/${id}`, data);
  return res;
}

async function deleteNganh(id: number): Promise<ApiResponse<string>> {
  const res: ApiResponse<string> = await api.delete(`/nganh/${id}`);
  return res;
}

export {
  getAllNganh,
  getNganhPage,
  createNganh,
  updateNganh,
  deleteNganh,
  type NganhRequest,
  type NganhResponse,
};

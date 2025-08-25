// src/services/lop.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/apiResponse";

// ===== DTO từ backend =====
interface LopRequest {
  tenLop: string;
  nganhId: number;
}

interface LopResponse {
  id: number;
  tenLop: string | null;
  nganhId: number | null;
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

// Lấy Page (nếu muốn phân trang sau này)
async function getLopPage(params?: {
  page?: number;
  size?: number;
  sort?: string;
}): Promise<ApiResponse<Page<LopResponse>>> {
  const res: ApiResponse<Page<LopResponse>> = await api.get("/lop", {
    params: {
      page: params?.page ?? 0,
      size: params?.size ?? 1000,
      sort: params?.sort ?? "updatedAt,DESC",
    },
  });
  return res;
}

// Lấy tất cả -> mảng
async function getAllLop(): Promise<ApiResponse<LopResponse[]>> {
  const pageRes = await getLopPage({
    page: 0,
    size: 1000,
    sort: "updatedAt,DESC",
  });
  return {
    code: pageRes.code,
    message: pageRes.message,
    result: pageRes.result?.content ?? [],
  };
}

async function createLop(data: LopRequest): Promise<ApiResponse<LopResponse>> {
  const res: ApiResponse<LopResponse> = await api.post("/lop", data);
  return res;
}

async function updateLop(
  id: number,
  data: LopRequest
): Promise<ApiResponse<LopResponse>> {
  const res: ApiResponse<LopResponse> = await api.put(`/lop/${id}`, data);
  return res;
}

async function deleteLop(id: number): Promise<ApiResponse<string>> {
  const res: ApiResponse<string> = await api.delete(`/lop/${id}`);
  return res;
}

export {
  getAllLop,
  getLopPage,
  createLop,
  updateLop,
  deleteLop,
  type LopRequest,
  type LopResponse,
};

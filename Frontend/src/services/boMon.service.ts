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

interface TruongBoMonCreationRequest {
  giangVienId: number;
  boMonId: number;
}

interface TruongBoMonCreationResponse {
  maGV: string;
  hoTen: string;
  hocVi?: string;
  hocHam?: string;
  tenBoMon: string;
}

type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

export interface BoMonWithTruongBoMonResponse {
  id: number;
  tenBoMon: string;
  khoaId: number;
  tenKhoa?: string;
  truongBoMonHoTen?: string | null; // có thể null nếu chưa gán
}

// ====================
// Service functions
// ====================

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

async function createTruongBoMon(
  data: TruongBoMonCreationRequest
): Promise<ApiResponse<TruongBoMonCreationResponse>> {
  const res: ApiResponse<TruongBoMonCreationResponse> = await api.post(
    "/bo-mon/truong-bo-mon",
    data
  );
  return res;
}

// GET /api/v1/bo-mon/with-truong-bo-mon
export async function getBoMonWithTBMPage(params?: {
  page?: number;
  size?: number;
  sort?: string;
}): Promise<ApiResponse<Page<BoMonWithTruongBoMonResponse>>> {
  const res: ApiResponse<Page<BoMonWithTruongBoMonResponse>> = await api.get(
    "/bo-mon/with-truong-bo-mon",
    {
      params: {
        page: params?.page ?? 0,
        size: params?.size ?? 10, // lấy nhiều để dựng map nhanh
        sort: params?.sort ?? "updatedAt,DESC",
      },
    }
  );
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
  createTruongBoMon,
  type BoMonRequest,
  type BoMonResponse,
  type TruongBoMonCreationRequest,
  type TruongBoMonCreationResponse,
};

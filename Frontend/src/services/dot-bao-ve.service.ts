// src/services/dot-bao-ve.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

export interface DotBaoVeRequest {
  tenDotBaoVe: string;
  hocKi: number;
  thoiGianBatDau: string;   // yyyy-mm-dd
  thoiGianKetThuc: string;  // yyyy-mm-dd
  namBatDau: number;
  namKetThuc: number;
}

export interface DotBaoVeResponse {
  id: number;
  tenDotBaoVe: string;
  hocKi: number;
  thoiGianBatDau: string;
  thoiGianKetThuc: string;
  namBatDau: number;
  namKetThuc: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;   // 0-index
  first?: boolean;
  last?: boolean;
}

export async function getDotBaoVePage(params?: {
  page?: number;     // 0-index
  size?: number;
  sort?: string;     // ví dụ: "updatedAt,DESC"
}): Promise<ApiResponse<Page<DotBaoVeResponse>>> {
  const res: ApiResponse<Page<DotBaoVeResponse>> = await api.get("/dot-bao-ve", {
    params: {
      page: params?.page ?? 0,
      size: params?.size ?? 10,
      sort: params?.sort ?? "updatedAt,DESC",
    },
  });
  return res;
}

export async function createDotBaoVe(data: DotBaoVeRequest) {
  const res: ApiResponse<DotBaoVeResponse> = await api.post("/dot-bao-ve", data);
  return res;
}

export async function updateDotBaoVe(id: number, data: DotBaoVeRequest) {
  const res: ApiResponse<DotBaoVeResponse> = await api.put(`/dot-bao-ve/${id}`, data);
  return res;
}

export async function deleteDotBaoVe(id: number) {
  const res: ApiResponse<string> = await api.delete(`/dot-bao-ve/${id}`);
  return res;
}

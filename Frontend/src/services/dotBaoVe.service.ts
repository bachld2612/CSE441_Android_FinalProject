// src/services/dot-bao-ve.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/apiResponse";

export interface DotBaoVeRequest {
  tenDotBaoVe: string;
  hocKi: number;
  thoiGianBatDau: string; // yyyy-mm-dd
  thoiGianKetThuc: string; // yyyy-mm-dd
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
  number: number; // 0-index
  first?: boolean;
  last?: boolean;
}

export async function getDotBaoVePage(params?: {
  page?: number; // 0-index
  size?: number;
  sort?: string; // ví dụ: "updatedAt,DESC"
}): Promise<ApiResponse<Page<DotBaoVeResponse>>> {
  const res: ApiResponse<Page<DotBaoVeResponse>> = await api.get(
    "/dot-bao-ve",
    {
      params: {
        page: params?.page ?? 0,
        size: params?.size ?? 10,
        sort: params?.sort ?? "updatedAt,DESC",
      },
    }
  );
  return res;
}

export async function createDotBaoVe(data: DotBaoVeRequest) {
  const res: ApiResponse<DotBaoVeResponse> = await api.post(
    "/dot-bao-ve",
    data
  );
  return res;
}

export async function updateDotBaoVe(id: number, data: DotBaoVeRequest) {
  const res: ApiResponse<DotBaoVeResponse> = await api.put(
    `/dot-bao-ve/${id}`,
    data
  );
  return res;
}

export async function deleteDotBaoVe(id: number) {
  const res: ApiResponse<string> = await api.delete(`/dot-bao-ve/${id}`);
  return res;
}

export type DotBaoVeOption = {
  value: number;
  label: string;
  data: DotBaoVeResponse;
};

export async function getDotBaoVeList(params?: {
  size?: number;
  sort?: string;
}): Promise<DotBaoVeResponse[]> {
  const res: ApiResponse<Page<DotBaoVeResponse>> = await api.get(
    "/dot-bao-ve",
    {
      params: {
        page: 0,
        size: params?.size ?? 1000,
        sort: params?.sort ?? "thoiGianBatDau,ASC",
      },
    }
  );
  return res.result?.content ?? [];
}

export async function getDotBaoVeOptions(): Promise<DotBaoVeOption[]> {
  const list = await getDotBaoVeList();
  return list.map((d) => {
    const label = `${d.tenDotBaoVe} • HK${d.hocKi} • ${d.namBatDau}`;
    return { value: d.id, label, data: d };
  });
}

export function findDotBaoVeId(
  list: DotBaoVeResponse[],
  criteria: {
    hocKi?: number;
    namBatDau?: number;
    dotThu?: number;
    tenDotBaoVe?: string;
  }
): number | null {
  const norm = (s: string) =>
    (s || "")
      .toLowerCase()
      .normalize("NFD")
      .replace(/\p{Diacritic}/gu, "");

  let targetName: string | undefined;
  if (criteria.dotThu && Number.isFinite(criteria.dotThu)) {
    targetName = `Đợt ${criteria.dotThu}`;
  } else if (criteria.tenDotBaoVe) {
    targetName = criteria.tenDotBaoVe;
  }

  let filtered = list;
  if (targetName) {
    filtered = filtered.filter(
      (d) => norm(d.tenDotBaoVe) === norm(targetName!)
    );
  }

  if (criteria.hocKi)
    filtered = filtered.filter((d) => d.hocKi === criteria.hocKi);
  if (criteria.namBatDau)
    filtered = filtered.filter((d) => d.namBatDau === criteria.namBatDau);

  filtered.sort(
    (a, b) => +new Date(b.thoiGianBatDau) - +new Date(a.thoiGianBatDau)
  );

  return filtered.length ? filtered[0].id : null;
}

export async function getDotBaoVeIdBy(criteria: {
  hocKi?: number;
  namBatDau?: number;
  dotThu?: number;
  tenDotBaoVe?: string;
}): Promise<number | null> {
  const list = await getDotBaoVeList();
  return findDotBaoVeId(list, criteria);
}

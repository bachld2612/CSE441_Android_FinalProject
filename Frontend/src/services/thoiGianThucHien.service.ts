import api from "@/lib/axios";

export interface ThoiGianThucHien {
  id: number;
  congViec: string;          // "DANG_KY_DE_TAI" | "NOP_DE_CUONG"
  thoiGianBatDau: string;    // yyyy-MM-dd
  thoiGianKetThuc: string;   // yyyy-MM-dd
  tenDotBaoVe: string;
  // nếu backend có trả thêm dotBaoVeId thì nên khai báo:
  // dotBaoVeId?: number;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
}
export interface Page<T> {
  content: T[];
  pageable: Pageable;
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
export interface ApiResponse<T> {
  code: number;
  message?: string;
  result?: T;
}

export type ThoiGianThucHienCreateRequest = {
  congViec: "DANG_KY_DE_TAI" | "NOP_DE_CUONG";
  thoiGianBatDau: string;    // yyyy-MM-dd
  thoiGianKetThuc: string;   // yyyy-MM-dd
  dotBaoVeId: number;
};

export const getThoiGianThucHienPage = async (
  args: { page: number; size: number }
): Promise<ApiResponse<Page<ThoiGianThucHien>>> => {
  const res: ApiResponse<Page<ThoiGianThucHien>> = await api.get(
    "/thoi-gian-thuc-hien",
    {
      params: {
        "pageable.page": args.page,
        "pageable.size": args.size,
      },
    }
  );
  return res;
};

export const createThoiGianThucHien = async (
  body: ThoiGianThucHienCreateRequest
): Promise<ApiResponse<ThoiGianThucHien>> => {
  const res: ApiResponse<ThoiGianThucHien> = await api.post(
    "/thoi-gian-thuc-hien",
    body
  );
  return res;
};

// ❗ Chặn id rỗng/undefined để tránh gọi /undefined
export const updateThoiGianThucHien = async (
  thoiGianThucHienId: number,
  body: ThoiGianThucHienCreateRequest
): Promise<ApiResponse<ThoiGianThucHien>> => {
  if (!Number.isFinite(thoiGianThucHienId)) {
    throw new Error("Invalid thoiGianThucHienId");
  }
  const idStr = encodeURIComponent(String(thoiGianThucHienId));
  const res: ApiResponse<ThoiGianThucHien> = await api.put(
    `/thoi-gian-thuc-hien/${idStr}`,
    body
  );
  return res;
};

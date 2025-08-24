import api from "@/lib/axios";
import type { ApiResponse } from "@/types/api-response";

// ===== DTO từ backend =====
export interface ThongBaoRequest {
  tieuDe: string;
  noiDung: string;
  file?: File | null; // optional; nếu backend chưa sửa required=false thì khi không có file sẽ lỗi 400
}

export interface ThongBaoResponse {
  id: number;
  tieuDe: string;
  noiDung: string;
  fileUrl?: string | null;
  createdAt: string; // LocalDate (yyyy-MM-dd)
  // Khuyến nghị backend bổ sung: updatedAt?: string
}

// Spring Data Page (giữ bố cục giống lop.service.ts)
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

// ===== APIs =====

// GET /api/v1/thong-bao (Page)
async function getThongBaoPage(params?: {
  page?: number;
  size?: number;
  sort?: string;
}): Promise<ApiResponse<Page<ThongBaoResponse>>> {
  const res: ApiResponse<Page<ThongBaoResponse>> = await api.get("/thong-bao", {
    params: {
      page: params?.page ?? 0,
      size: params?.size ?? 10,
      sort: params?.sort ?? "updatedAt,DESC",
    },
  });
  return res;
}

// GET all -> mảng (lấy nhiều, mặc định 1000)
async function getAllThongBao(): Promise<ApiResponse<ThongBaoResponse[]>> {
  const pageRes = await getThongBaoPage({
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

// GET /api/v1/thong-bao/{id}
async function getThongBaoById(id: number): Promise<ApiResponse<ThongBaoResponse>> {
  const res: ApiResponse<ThongBaoResponse> = await api.get(`/thong-bao/${id}`);
  return res;
}

// POST /api/v1/thong-bao (multipart/form-data)
async function createThongBao(data: ThongBaoRequest): Promise<ApiResponse<ThongBaoResponse>> {
  const form = new FormData();
  form.set("tieuDe", data.tieuDe);
  form.set("noiDung", data.noiDung);
  if (data.file) form.set("file", data.file);

  // Quan trọng: override Content-Type vì axios base đang set application/json
  const res: ApiResponse<ThongBaoResponse> = await api.post("/thong-bao", form, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res;
}

export {
  getThongBaoPage,
  getAllThongBao,
  getThongBaoById,
  createThongBao,
  type Page,
};

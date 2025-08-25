import api from "@/lib/axios";
import type {
  ApiResponse,
  PageResponse,
  PageableRequest,
} from "@/types/apiResponse";

// Dạng dữ liệu đúng theo DeCuongResponse trong swagger mẫu
export interface DeCuong {
  id: number;
  deCuongUrl?: string | null;
  trangThai: "PENDING" | "ACCEPTED" | "REJECTED" | "CANCELED";
  soLanNop?: number | null;
  nhanXet?: string | null;
  tenDeTai: string;
  maSV: string;
  hoTenSinhVien: string;
  hoTenGiangVien?: string | null;

  // cho UI hiển thị tên file
  deCuongFilename?: string | null;
}

const filenameFromUrl = (url?: string | null) => {
  if (!url) return null;
  try {
    const clean = url.split("?")[0];
    const parts = clean.split("/");
    return parts[parts.length - 1] || null;
  } catch {
    return null;
  }
};

export const getDeCuongApproval = async (
  pageable: PageableRequest
): Promise<PageResponse<DeCuong>> => {
  // ❗ KHÔNG thêm /api/v1 ở đây vì baseURL đã có rồi
  const res: ApiResponse<PageResponse<DeCuong>> = await api.get("/de-cuong", {
    params: pageable, // { page, size, sort }
  });

  const page = res.result!;
  page.content = page.content.map((it: any) => ({
    ...it,
    deCuongFilename: it.deCuongFilename ?? filenameFromUrl(it.deCuongUrl),
  }));

  return page;
};

// ✅ Duyệt: không cần reason, không body
export async function approveDeCuong(id: number) {
  return api.put(`/de-cuong/${id}/duyet`);
}

// ❗ Từ chối: bắt buộc reason (query hoặc body)
// dùng query cho đơn giản
export async function rejectDeCuong(id: number, reason: string) {
  return api.put(`/de-cuong/${id}/tu-choi`, null, { params: { reason } });
}

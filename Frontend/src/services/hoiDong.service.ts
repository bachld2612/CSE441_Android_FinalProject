import api from "@/lib/axios";
import type { ApiResponse, PageResponse, PageableRequest } from "@/types/api-response";
import type {
  HoiDongCreateRequest,
  HoiDongDetail,
  HoiDongListItem,
  ImportResult,
  HoiDongType,
} from "@/types/hoiDong.types";

export async function getHoiDongPage(
  pageable: PageableRequest & { q?: string; loai?: HoiDongType | "ALL"; dotBaoVeId: number }
): Promise<PageResponse<HoiDongListItem>> {
  const params: Record<string, any> = {
    dotBaoVeId: pageable.dotBaoVeId, 
    page: pageable.page,
    size: pageable.size,
  };
  if (pageable.q) params.keyword = pageable.q;
  if (pageable.loai && pageable.loai !== "ALL") params.type = pageable.loai;

  const res: ApiResponse<PageResponse<HoiDongListItem>> = await api.get(
    "/hoi-dong/hoi-dong-theo-dot",
    { params }
  );
  return res.result!;
}

export async function createHoiDong(body: HoiDongCreateRequest) {
  const res: ApiResponse<any> = await api.post("/hoi-dong/them-hoi-dong", body);
  return res;
}

export async function getHoiDongDetail(id: number): Promise<HoiDongDetail> {
  const res: ApiResponse<HoiDongDetail> = await api.get(`/hoi-dong/${id}`);
  return res.result!;
}

export async function importSinhVienToHoiDong(id: number, file: File): Promise<ImportResult> {
  const fd = new FormData();
  fd.append("file", file);

  const res: ApiResponse<ImportResult> = await api.post(
    `/hoi-dong/${id}/import-sinh-vien`,
    fd,
    {
      headers: { "Content-Type": "multipart/form-data" },
      transformRequest: [(data) => data],
    }
  );
  return res.result!;
}
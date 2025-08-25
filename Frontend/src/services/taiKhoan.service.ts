// src/services/tai-khoan.service.ts
import api from "@/lib/axios";
import type { ApiResponse } from "@/types/apiResponse";

// ===== DTO từ backend =====
interface AnhDaiDienUploadResponse {
  anhDaiDienUrl: string;
}

// ===== Service =====
async function uploadAnhDaiDien(
  file: File
): Promise<ApiResponse<AnhDaiDienUploadResponse>> {
  const form = new FormData();
  // TÊN FIELD PHẢI LÀ 'file' đúng với backend
  form.append("file", file);

  const res: ApiResponse<AnhDaiDienUploadResponse> = await api.post(
    "/tai-khoan/anh-dai-dien",
    form,
    {
      // override header JSON mặc định => multipart
      headers: { "Content-Type": "multipart/form-data" },
    }
  );

  return res;
}

export { uploadAnhDaiDien, type AnhDaiDienUploadResponse };

import api from "@/lib/axios";

export interface SinhVien {
  maSV: string;
  hoTen: string;
  soDienThoai: string;
  email: string;
  tenLop: string;
  kichHoat: boolean;
}

export type SinhVienCreationRequest = {
    maSV: string;
    hoTen: string;
    soDienThoai: string;
    email: string;
    matKhau: string;
    lopId: number; 
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface ApiResponse<T> {
  code: number;
  result: T;
  message?: string;
}

export interface PageableRequest {
  page: number;
  size: number;
  sort?: string; // ví dụ: "lop.tenLop,asc"
}

const getAllSinhVien = async (
  pageable: PageableRequest
): Promise<PageResponse<SinhVien>> => {
  const res: ApiResponse<PageResponse<SinhVien>> = await api.get("/sinh-vien", {
    params: {
      page: pageable.page,
      size: pageable.size,
      sort: pageable.sort,
    },
  });
  console.log("SinhVienService - getAllSinhVien response:", res);
  return res.result; // ⚡ interceptor đã trả về data, nên res = { code, result }
};

const createSinhVien = async (
  sinhVien: SinhVienCreationRequest
): Promise<ApiResponse<SinhVien>> => {
  const res: ApiResponse<SinhVien> = await api.post("/sinh-vien", sinhVien);
  console.log("SinhVienService - createSinhVien response:", res);
  return res;
};

export { getAllSinhVien, createSinhVien };

export interface SinhVienOfGiangVien {
  maSV: string;
  hoTen: string;
  tenLop: string;
  soDienThoai?: string;
  tenDeTai?: string;
}

const getSinhVienOfGiangVien = async (
  pageable: PageableRequest
): Promise<PageResponse<SinhVienOfGiangVien>> => {
  const res: ApiResponse<PageResponse<SinhVienOfGiangVien>> = await api.get(
    "/giang-vien/sinh-vien",
    { params: { page: pageable.page, size: pageable.size, sort: pageable.sort } }
  );
  return res.result;
};

export { getSinhVienOfGiangVien };
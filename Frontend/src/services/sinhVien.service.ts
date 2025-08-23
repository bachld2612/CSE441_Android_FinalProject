import api from "@/lib/axios";

export interface SinhVien {
  maSV: string;
  hoTen: string;
  soDienThoai: string;
  email: string;
  tenLop: string;
  kichHoat: boolean;
}

export interface SinhVienInfoResponse{
  maSV?: string;
  hoTen?: string;
  soDienThoai?: string;
  email?: string;
  tenLop?: string;
  tenKhoa?: string;
  tenNganh?: string;
  cvUrl?: string;
}

export interface SinhVienWihtoutDeTai {
  maSV: string;
  hoTen: string;
}

export type SinhVienCreationRequest = {
  maSV: string;
  hoTen: string;
  soDienThoai: string;
  email: string;
  matKhau: string;
  lopId: number;
};

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
  sort?: string;
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
  return res.result;
};

const findSinhVienByInfo = async (
  info: string,
  pageable: PageableRequest
): Promise<PageResponse<SinhVien>> => {
  const res: ApiResponse<PageResponse<SinhVien>> = await api.get(
    "/sinh-vien/search",
    {
      params: {
        page: pageable.page,
        size: pageable.size,
        sort: pageable.sort,
        info: info,
      },
    }
  );
  console.log("SinhVienService - findSinhVienByInfo response:", res);
  return res.result;
};

const createSinhVien = async (
  sinhVien: SinhVienCreationRequest
): Promise<ApiResponse<SinhVien>> => {
  try {
    const res: ApiResponse<SinhVien> = await api.post("/sinh-vien", sinhVien);
    console.log("SinhVienService - createSinhVien response:", res);
    return res;
  } catch (error) {
    console.error("SinhVienService - createSinhVien error:", error);
    throw error; // Ném lỗi để xử lý ở nơi gọi
  }
};

const importSinhVien = async (file: File): Promise<ApiResponse<string>> => {
  const formData = new FormData();
  formData.append("file", file);
  const res: ApiResponse<string> = await api.post(
    "/sinh-vien/import",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
  console.log("SinhVienService - importSinhVien response:", res);
  return res;
};

const changeSinhVienStatus = async (
  maSV: string
): Promise<ApiResponse<string>> => {
  try {
    const res: ApiResponse<string> = await api.put(
      `/sinh-vien/change-status/${maSV}`
    );
    console.log("SinhVienService - changeSinhVienStatus response:", res);
    return res;
  } catch (error) {
    console.error("SinhVienService - changeSinhVienStatus error:", error);
    throw error;
  }
};

const updateSinhVien = async (
  maSV: string,
  sinhVien: SinhVienCreationRequest
): Promise<ApiResponse<SinhVien>> => {
  try {
    const res: ApiResponse<SinhVien> = await api.put(
      `/sinh-vien/${maSV}`,
      sinhVien
    );
    console.log("SinhVienService - updateSinhVien response:", res);
    return res;
  } catch (error) {
    console.error("SinhVienService - updateSinhVien error:", error);
    throw error; 
  }
};
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

const getSinhVienWithoutDeTai = async (): Promise<ApiResponse<SinhVienWihtoutDeTai[]>> => {
  try{
    const res: ApiResponse<SinhVienWihtoutDeTai[]> = await api.get(
    "/sinh-vien/without-de-tai"
  );
  return res;
  }catch (error) {
    console.error("SinhVienService - getSinhVienWithoutDeTai error:", error);
    throw error; 
  }
};

const getSinhVienByMaSV = async (maSV: string): Promise<ApiResponse<SinhVienInfoResponse>> => {
  try {
    const res: ApiResponse<SinhVienInfoResponse> = await api.get(`/sinh-vien/${maSV}`);
    return res;
  } catch (error) {
    console.error("SinhVienService - getSinhVienByMaSV error:", error);
    throw error;
  }
};

export {
  getAllSinhVien,
  createSinhVien,
  importSinhVien,
  findSinhVienByInfo,
  changeSinhVienStatus,
  updateSinhVien,
  getSinhVienOfGiangVien,
  getSinhVienWithoutDeTai,
  getSinhVienByMaSV
};
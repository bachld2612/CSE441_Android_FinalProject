import axios from "axios";
import api from "@/lib/axios";
import { create } from "zustand";

export type Role = "TRO_LY_KHOA" | "GIANG_VIEN" | "SINH_VIEN" | "ADMIN";

export type User = {
  maSV?: string;
  email?: string;
  maGV?: string;
  hoTen?: string;
  soDienThoai?: string;
  hocVi?: string;
  hocHam?: string;
  lop?: string;
  nganh?: string;
  boMon?: string;
  khoa?: string;
  role?: Role;
  anhDaiDienUrl?: string;
};

type AuthState = {
  token: string | null;
  user: User | null;
  ready: boolean;

  login: (data: LoginRequest) => Promise<LoginResponse>;
  logout: () => Promise<LogoutResponse> | Promise<LoginResponse>;
  getMyInfo: () => Promise<MyInfoResponse>;
  introspect: () => Promise<boolean>;
};

interface IntrospectResponse {
  code: number;
  result: { valid: string };
}

interface LoginResponse {
  code: number;
  message?: string;
  result?: { token?: string };
}

interface LogoutResponse {
  code: number;
  message?: string;
  result?: string;
}

interface MyInfoResponse {
  code: number;
  message?: string;
  result?: User;
}

interface LoginRequest {
  email: string;
  matKhau: string;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem("token") || null,
  user: null,
  ready: false,

  login: async (data: LoginRequest) => {
    try {
      const response: LoginResponse = await api.post("/auth/login", data);
      if (response.code === 1000 && response.result?.token) {
        localStorage.setItem("token", response.result.token);
        set({ token: response.result.token });
        await get().getMyInfo();
      }
      return response;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return (error.response?.data || { code: 500, message: "Lỗi không xác định" }) as LoginResponse;
      }
      return { code: 500, message: "Đã xảy ra lỗi không mong muốn" };
    }
  },

  logout: async () => {
    try {
      const response: LogoutResponse = await api.post("/auth/logout", { token: get().token });
      if (response.code === 1000) {
        localStorage.removeItem("token");
        set({ token: null, user: null, ready: false });
      }
      return response;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return (error.response?.data || { code: 500, message: "Lỗi không xác định" }) as LogoutResponse;
      }
      return { code: 500, message: "Đã xảy ra lỗi không mong muốn" } as LogoutResponse;
    }
  },

  getMyInfo: async () => {
    try {
      const response: MyInfoResponse = await api.get("/auth/my-info");
      if (response.code === 1000) set({ user: response.result ?? null, ready: true });
      else set({ user: null, ready: true });
      return response;
    } catch (error) {
      set({ user: null, ready: true });
      if (axios.isAxiosError(error)) {
        return (error.response?.data || { code: 500, message: "Lỗi không xác định" }) as MyInfoResponse;
      }
      return { code: 500, message: "Đã xảy ra lỗi không mong muốn" };
    }
  },

  introspect: async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return false;
      const response: IntrospectResponse = await api.post("/auth/introspect", { token });
      return response.code === 1000 && Boolean(response.result?.valid);
    } catch {
      return false;
    }
  },
}));

import axios from "axios";
import api from "../lib/axios";
import { create } from "zustand";

type AuthState = {
  token: string | null;
  login: (data: LoginRequest) => Promise<LoginResponse>;
  logout: () => Promise<LogoutResponse> | Promise<LoginResponse>;
  getMyInfo: () => Promise<MyInfoResponse>;
  introspect: () => Promise<boolean>;
};


interface IntrospectResponse {
  code: number;
  result:{
    valid: string
  }
}

interface LoginResponse {
  code: number;
  message?: string;
  result?: {
    token?: string;
  }
}

interface LogoutResponse {
  code: number;
  message?: string;
  result?: string
}


interface MyInfoResponse {
  code: number;
  message?: string;
  result?: {
    maSV?: string,
    email?: string,
    maGV?: string,
    hoTen?: string,
    soDienThoai?: string,
    hocVi?: string,
    hocHam?: string,
    lop?: string,
    nganh?: string,
    boMon?: string,
    khoa?: string,
    role?: "SINH_VIEN",
    anhDaiDienUrl?: string
  }
}

interface LoginRequest{
  email: string;
  matKhau: string;
}

export const useAuthStore = create<AuthState>(() => ({
  token: localStorage.getItem("token") || null,
  login: async (data: LoginRequest) => {
    try {
      const response: LoginResponse = await api.post("/auth/login", data);
      console.log("Login response:", response);
      if (response.code === 1000) {
        localStorage.setItem("token", response.result?.token || "");
        useAuthStore.setState({
          token: response.result?.token,
        });
      }
      return response;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return error.response?.data;
      }
      console.error("Unexpected error:", error);
      return { code: 500, message: "Đã xảy ra lỗi không mong muốn" };
    }
  },
  logout: async () => {
    try {
      const data: { token: string | null } = { token: useAuthStore.getState().token };
      const response: LogoutResponse = await api.post("/auth/logout", data);
      console.log("Logout response:", response);
      if (response.code === 1000) {
        localStorage.removeItem("token");
      }
      return response;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return error.response?.data;
      }
    }
  },

  getMyInfo: async () => {
    try {
      const response = await api.get("/auth/my-info");
      console.log("Get My Info response:", response);
      return response;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return error.response?.data;
      }
      console.error("Unexpected error:", error);
      return { code: 500, message: "Đã xảy ra lỗi không mong muốn" };
    }
  },

  introspect: async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        return false;
      }
      const response: IntrospectResponse = await api.post("/auth/introspect", {
        token: token,
      });
      console.log("Introspect response:", response);
      if (response.code === 1000 && response.result.valid) {
        return true;
      }
      return false;
    } catch (error) {
      console.error("Introspect error:", error);
      return false;
    }
  },
  
}));
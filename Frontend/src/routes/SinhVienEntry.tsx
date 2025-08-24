// import { useEffect } from "react";
// import { useAuthStore } from "@/stores/authStore";
// import SinhVienPage from "@/pages/SinhVienPage";          // trang cũ: quản lý sinh viên (trợ lý khoa)
// import SinhVienOfGiangVienPage from "@/pages/SinhVienOfGiangVienPage"; // trang mới: SV đăng ký của giảng viên

// export default function SinhVienEntry() {
//   const { token, ready, user, getMyInfo } = useAuthStore();


//   useEffect(() => { if (token && !ready) getMyInfo(); }, [token, ready, getMyInfo]);

//   if (!token) return <div className="p-6">Bạn chưa đăng nhập.</div>;
//   if (!ready) return <div className="p-6">Đang tải thông tin...</div>;

//   if (user?.role === "ADMIN" || user?.role === "TRO_LY_KHOA") return <SinhVienPage />;
//   if (user?.role === "GIANG_VIEN")  return <SinhVienOfGiangVienPage />;

//   return <div className="p-6">Bạn không có quyền truy cập trang này.</div>;
// }

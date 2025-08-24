import Header from "@/components/Header";
import SidebarHoiDong from "@/components/SideBarHoiDong";
import { Outlet } from "react-router-dom";

export default function HoiDongLayout() {
  return (
    <div className="flex min-h-screen">
      <SidebarHoiDong />
      <div className="flex-1 flex flex-col">
        <Header />
        <main className="flex-1 p-6 mt-[64px] ml-64 bg-gray-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

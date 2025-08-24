import Header from "@/components/Header";
import { Outlet } from "react-router-dom";
import SidebarToChuc from "@/components/SidebarToChuc";

export default function ToChucLayout() {
  return (
    <div className="flex min-h-screen">
      {/* Sidebar cố định */}
      <SidebarToChuc />

      {/* Phần chính */}
      <div className="flex-1 flex flex-col">
        {/* Header giống MainLayout */}
        <Header />
        {/* Nội dung: chừa chỗ cho header + sidebar */}
        <main className="flex-1 p-6 mt-[64px] ml-64 ">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

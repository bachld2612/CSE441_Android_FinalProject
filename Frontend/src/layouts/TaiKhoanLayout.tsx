// src/layouts/TaiKhoanLayout.tsx
import Header from "@/components/Header";
import { Outlet } from "react-router-dom";

export default function TaiKhoanLayout() {
  return (
    <div>
      <Header />
      {/* Page content */}
      <main className="container flex-1 p-6 mt-[100px] mx-auto">
        <Outlet />
      </main>
    </div>
  );
}

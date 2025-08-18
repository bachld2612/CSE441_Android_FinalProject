// src/layouts/DoAnLayout.tsx
import Header from "@/components/Header";
import { Outlet } from "react-router-dom";
import SidebarDoAn from "@/components/SidebarDoAn";

export default function DoAnLayout() {
  return (
    <div className="flex min-h-screen">
      <SidebarDoAn />
      <div className="flex-1 flex flex-col">
        <Header />
        <main className="flex-1 p-6 mt-[64px] ml-64 bg-gray-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

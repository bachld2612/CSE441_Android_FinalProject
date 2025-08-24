// src/layouts/MainLayout.tsx
import Header from "@/components/Header";
import { Outlet, } from "react-router-dom";

export default function MainLayout() {
  return (
    <div className="flex min-h-screen">
      <div className="flex-1 flex flex-col">
        <Header/>
        <main className="flex-1 p-6 mt-[64px] container mx-auto ">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

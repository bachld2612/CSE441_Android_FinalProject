// src/layouts/MainLayout.tsx
import Header from "@/components/Header";
import { Outlet, NavLink } from "react-router-dom";

export default function MainLayout() {
  return (
    <div className="flex min-h-screen">
      {/* Sidebar */}
      <aside className="w-64 h-full bg-[#457B9D] fixed top-[64px] text-white flex flex-col">
        <div className="p-4 font-bold text-lg border-b border-white/20">
          My App
        </div>
        <nav className="flex-1 p-4 space-y-2">
          <NavLink
            to="/"
            className={({ isActive }) =>
              `block px-3 py-2 rounded-lg ${
                isActive ? "bg-white text-[#457B9D]" : "hover:bg-white/20"
              }`
            }
          >
            Home
          </NavLink>
          <NavLink
            to="/auth"
            className={({ isActive }) =>
              `block px-3 py-2 rounded-lg ${
                isActive ? "bg-white text-[#457B9D]" : "hover:bg-white/20"
              }`
            }
          >
            Auth Page
          </NavLink>
        </nav>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <Header/>

        {/* Page content */}
        <main className="flex-1 p-6 mt-[64px] ml-64 bg-gray-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

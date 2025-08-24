import { NavLink } from "react-router-dom";

export default function SidebarHoiDong() {
  return (
    <aside className="w-64 h-full bg-[#457B9D] fixed top-[64px] text-white flex flex-col">
      <div className="p-4 font-bold text-lg border-b border-white/20">
        Quản lý Hội đồng
      </div>

      <nav className="flex-1 p-4 space-y-2">
        <NavLink
          to="/hoi-dong/bao-ve"
          className={({ isActive }) =>
            `block px-3 py-2 rounded-lg ${
              isActive ? "bg-white text-[#457B9D]" : "hover:bg-white/20"
            }`
          }
          end
        >
          Hội đồng bảo vệ
        </NavLink>

        <NavLink
          to="/hoi-dong/phan-bien"
          className={({ isActive }) =>
            `block px-3 py-2 rounded-lg ${
              isActive ? "bg-white text-[#457B9D]" : "hover:bg-white/20"
            }`
          }
          end
        >
          Hội đồng phản biện
        </NavLink>
      </nav>
    </aside>
  );
}

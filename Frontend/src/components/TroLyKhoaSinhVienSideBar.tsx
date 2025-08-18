import { NavLink } from "react-router-dom";

const sidebarLinks = [
  { name: "Quản lý sinh viên", href: "/sinh-vien" },
  { name: "Đăng kí giảng viên hướng dẫn", href: "/sinh-vien/gvhd" },
];

export default function TroLyKhoaSinhVienSideBar() {
  return (
    <aside className="w-64 h-full fixed top-[64px] left-0 bg-[#F1F1F3] text-gray-900 border-r border-gray-200">
      <nav className="p-4 space-y-1">
        {sidebarLinks.map((link) => (
          <NavLink
            key={link.href}
            to={link.href}
            end
            className={({ isActive }) =>
              [
                "block rounded px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-[#EFF6FF] text-[#006EFF]"
                  : "text-gray-800 hover:bg-gray-200 hover:text-gray-900",
              ].join(" ")
            }
          >
            {link.name}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

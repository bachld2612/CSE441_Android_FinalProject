import { NavLink } from "react-router-dom";

const sidebarLinks = [
  { name: "Sinh viên đăng ký đồ án", href: "/sinh-vien/dang-ky-do-an" },
  { name: "Sinh viên hướng dẫn", href: "/sinh-vien/huong-dan" },
];

export default function SidebarSinhVien() {
  return (
    <aside className="w-64 h-full bg-[#457B9D] fixed top-[64px] text-white flex flex-col">
      <div className="p-4 font-bold text-lg border-b border-white/20">
        Quản lý sinh viên
      </div>
      <nav className="flex-1 p-4 space-y-2">
        {sidebarLinks.map((link) => (
          <NavLink
            key={link.href}
            to={link.href}
            className={({ isActive }) =>
              `block px-3 py-2 rounded-lg ${
                isActive
                  ? "bg-white text-[#457B9D] font-semibold"
                  : "hover:bg-white/20"
              }`
            }
          >
            {link.name}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";

const sidebarLinks = [
  {
    name: "Quản lý sinh viên",
    href: "/sinh-vien",
    roles: ["tro_ly_khoa", "admin"],
  },
  {
    name: "Đăng ký giảng viên hướng dẫn",
    href: "/sinh-vien/gvhd",
    roles: ["tro_ly_khoa", "admin"],
  },
  {
    name: "Sinh viên hướng dẫn",
    href: "/sinh-vien/huong-dan",
    roles: ["giang_vien", "truong_bo_mon", "tro_ly_khoa"],
  },
];

export default function SidebarSinhVien() {
  const [role, setRole] = useState(null);

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        setRole(parsedInfo.role.toLowerCase() || null);
        console.log("Parsed role:", parsedInfo.role);
      } catch (error) {
        console.error("Lỗi parse myInfo:", error);
      }
    }
  }, []);

  const filteredLinks = sidebarLinks.filter((link) =>
    role ? link.roles.includes(role) : false
  );

  return (
    (role === "tro_ly_khoa" || role == "admin") && (
      <aside className="w-64 h-full bg-[#457B9D] fixed top-[64px] text-white flex flex-col">
        <div className="p-4 font-bold text-lg border-b border-white/20">
          Quản lý sinh viên
        </div>
        <nav className="flex-1 p-4 space-y-2">
          {filteredLinks.map((link) => (
            <NavLink
              end
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
          {filteredLinks.length === 0 && (
            <div className="text-sm text-white/70">
              Bạn không có quyền truy cập menu nào
            </div>
          )}
        </nav>
      </aside>
    )
  );
}

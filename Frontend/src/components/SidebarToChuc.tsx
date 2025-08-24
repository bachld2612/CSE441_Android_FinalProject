import { NavLink } from "react-router-dom";
import { useEffect, useState } from "react";

export default function SidebarToChuc() {
  const [role, setRole] = useState<string | null>(null);

  useEffect(() => {
    const currentRole = localStorage.getItem("myInfo");
    if (currentRole) {
      const parsedRole = JSON.parse(currentRole).role;
      setRole(parsedRole);
    }
  }, []);

  // danh sách link mặc định
  const sidebarLinks = [
    { name: "Quản lý khoa", href: "/to-chuc/khoa" },
    { name: "Quản lý bộ môn", href: "/to-chuc/bo-mon" },
    { name: "Quản lý ngành", href: "/to-chuc/nganh" },
    { name: "Quản lý lớp", href: "/to-chuc/lop" },
  ];

  // chỉ TRO_LY_KHOA được thấy "Quản lý đợt đồ án"
  if (role === "TRO_LY_KHOA") {
    sidebarLinks.push({
      name: "Quản lý đợt đồ án",
      href: "/to-chuc/dot-do-an",
    });
    sidebarLinks.push({
      name: "Quản lý thời gian đồ án",
      href: "/to-chuc/thoi-gian-do-an",
    });
  }


  return (
    <aside className="w-64 h-full fixed top-[64px] left-0 bg-[#457B9D] text-white border-r border-gray-200">
      <div className="p-4 font-bold text-lg border-b border-white/20">
        Quản lý tổ chức
      </div>
      <nav className="flex-1 p-4 space-y-2">
        {sidebarLinks.map((link) => (
          <NavLink
            key={link.href}
            to={link.href}
            className={({ isActive }) =>
              [
                `block px-3 py-2 rounded-lg ${
                  isActive
                    ? "bg-white text-[#457B9D] font-semibold"
                    : "hover:bg-white/20"
                }`,
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

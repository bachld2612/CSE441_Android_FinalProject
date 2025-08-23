import { Bell, ChevronDown, LogOut, SquarePen } from "lucide-react";
import logo from "@/assets/tlu_logo 1.png";
import { Link, useLocation } from "react-router-dom";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@radix-ui/react-dropdown-menu";
import { useAuthStore } from "@/stores/authStore";
import { useEffect, useState } from "react";
import { DropdownMenuPortal } from "./ui/dropdown-menu";
import NotificationsPanel from "@/components/NotificationsPanel";

type MyInfo = {
  maSV?: string;
  email?: string;
  maGV?: string;
  hoTen?: string;
  soDienThoai?: string;
  hocVi?: string;
  hocHam?: string;
  lop?: string;
  nganh?: string;
  boMon?: string;
  khoa?: string;
  role?: "SINH_VIEN";
  anhDaiDienUrl?: string;
};

export default function Header() {
  const [myInfo, setMyInfo] = useState<MyInfo | null>(null);
  
  const [role, setRole] = useState(null);
  
  const navbarLinks = [
    { name: "Trang chủ", href: "/", hidden: false },
    { name: "Đồ án", href: "/do-an", hidden: role === "ADMIN" },
    { name: "Sinh viên", href: role === "GIANG_VIEN" || role === "TRUONG_BO_MON" ? "/sinh-vien/huong-dan" : "/sinh-vien", hidden: false },
    { name: "Giảng viên", href: "/giang-vien", hidden: false },
    { name: "Hội đồng", href: "/hoi-dong", hidden: false },
    { name: "Tổ chức", href: "/to-chuc/khoa", hidden: false },
    { name: "Thông báo hệ thống", href: "/thong-bao", hidden: role == "GIANG_VIEN" || role == "TRUONG_BO_MON" }
  ];

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        setRole(parsedInfo.role || null);
        console.log("Parsed role:", parsedInfo.role);
      } catch (error) {
        console.error("Lỗi parse myInfo:", error);
      }
    }
  }, []);

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      setMyInfo(JSON.parse(storedInfo));
      console.log("MyInfo from localStorage:", JSON.parse(storedInfo));
    }
  }, []);

  const location = useLocation();

  const handleLogout = async () => {
    await useAuthStore.getState().logout();
    window.location.href = "/login";
    localStorage.removeItem("myInfo");
  };

  return (
    <header className="bg-white w-screen fixed top-0 shadow-sm">
      <div className="max-w-full mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo + menu */}
          <div className="flex items-center space-x-8">
            {/* Logo */}
            <img
              src={logo}
              alt="TLU Logo"
              className="w-10 h-10 object-contain"
            />

            <nav className="flex space-x-4">
              {navbarLinks.map((item) => {
                const isActive =
                  location.pathname.startsWith(item.href) && item.href !== "/";
                const isHome = item.href === "/" && location.pathname === "/";

                return !item.hidden && (
                  <Link
                    key={item.href}
                    to={item.href}
                    className={`px-3 py-3 rounded-md text-sm font-medium ${
                      isActive || isHome
                        ? "bg-gray-200 text-gray-900"
                        : "text-gray-700 hover:bg-gray-50"
                    }`}
                  >
                    {item.name}
                  </Link>
                );
              })}
            </nav>
          </div>

          <div className="flex items-center space-x-6">
            <NotificationsPanel />

            {/* Avatar + name */}
            <div>
              <DropdownMenu>
                <DropdownMenuTrigger className="flex items-center space-x-2 cursor-pointer">
                  <img
                    src={myInfo?.anhDaiDienUrl}
                    alt="User"
                    className="w-8 h-8 rounded-full"
                  />
                  <span className="text-sm font-medium text-gray-800">
                    {myInfo?.hoTen ? myInfo?.hoTen : "User"}
                  </span>
                  <ChevronDown className="w-4 h-4 text-gray-600" />
                </DropdownMenuTrigger>
                <DropdownMenuPortal>
                  <DropdownMenuContent className=" bg-gray-100 p-5 flex flex-col space-y-2">
                    <DropdownMenuItem className="flex px-2 py-1 rounded-[4px] focus:outline-none focus:ring-0 hover:bg-gray-300 items-center gap-2">
                      <SquarePen className="w-4" />
                      Đổi mật khẩu
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={handleLogout}
                      className="flex px-2 py-1 rounded-[4px] focus:outline-none focus:ring-0 hover:bg-gray-300 items-center gap-2"
                    >
                      <LogOut className="w-4" />
                      Đăng xuất
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenuPortal>
              </DropdownMenu>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}

import { ChevronDown, LogOut, SquarePen } from "lucide-react";
import logo from "@/assets/tlu_logo 1.png";
import { Link, useLocation, useNavigate } from "react-router-dom";
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
  role?: "SINH_VIEN"; // bạn có thể mở rộng union nếu muốn
  anhDaiDienUrl?: string;
};

const DEFAULT_AVATAR =
  "https://graph.facebook.com/100000000000000/picture?type=large";

export default function Header() {
  const [myInfo, setMyInfo] = useState<MyInfo | null>(null);
  const navigate = useNavigate();

  const [role, setRole] = useState<string | null>(null);

  const navbarLinks = [
    { name: "Trang chủ", href: "/", hidden: false },
    { name: "Đồ án", href: "/do-an", hidden: role === "ADMIN" },
    {
      name: "Sinh viên",
      href:
        role === "GIANG_VIEN" || role === "TRUONG_BO_MON"
          ? "/sinh-vien/huong-dan"
          : "/sinh-vien",
      hidden: false,
    },
    { name: "Giảng viên", href: "/giang-vien", hidden: false },
    { name: "Hội đồng", href: "/hoi-dong", hidden: false },
    { name: "Tổ chức", href: "/to-chuc/khoa", hidden: false },
    {
      name: "Thông báo hệ thống",
      href: "/thong-bao",
      hidden: role == "GIANG_VIEN" || role == "TRUONG_BO_MON",
    },
  ];

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        setRole(parsedInfo.role || null);
      } catch (error) {
        console.error("Lỗi parse myInfo:", error);
      }
    }
  }, []);

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      setMyInfo(JSON.parse(storedInfo));
    }
  }, []);

  const location = useLocation();

  const handleLogout = async () => {
    await useAuthStore.getState().logout();
    window.location.href = "/login";
    localStorage.removeItem("myInfo");
  };

  const avatarSrc = myInfo?.anhDaiDienUrl || DEFAULT_AVATAR;

  return (
    <header className="bg-white w-screen fixed top-0 shadow-sm">
      <div className="max-w-full mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo + menu */}
          <div className="flex items-center space-x-8">
            {/* Logo */}
            <img src={logo} alt="TLU Logo" className="w-10 h-10 object-contain" />

            <nav className="flex space-x-4">
              {navbarLinks.map((item) => {
                const isActive =
                  location.pathname.startsWith(item.href) && item.href !== "/";
                const isHome = item.href === "/" && location.pathname === "/";

                return (
                  !item.hidden && (
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
                  )
                );
              })}
            </nav>
          </div>

          <div className="flex items-center gap-3 justify-center">
            <NotificationsPanel />

            {/* Avatar: CLICK = điều hướng /tai-khoan */}
            <img
              src={avatarSrc}
              alt="User"
              className="w-8 h-8 rounded-full cursor-pointer ml-2"
              onClick={() => navigate("/tai-khoan")}
              onError={(e) => {
                (e.currentTarget as HTMLImageElement).src = DEFAULT_AVATAR;
              }}
            />

            {/* Dropdown: chỉ kích hoạt khi bấm vào chữ (tên) + chevron */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button
                  type="button"
                  className="flex items-center space-x-2 cursor-pointer"
                  aria-label="Open user menu"
                >
                  <span className="text-sm font-medium text-gray-800">
                    {myInfo?.hoTen ? myInfo.hoTen : "User"}
                  </span>
                  <ChevronDown className="w-4 h-4 text-gray-600" />
                </button>
              </DropdownMenuTrigger>

              <DropdownMenuPortal>
                <DropdownMenuContent className="bg-gray-100 p-5 flex flex-col space-y-2 rounded-md shadow-lg">
                  <DropdownMenuItem
                    className="flex px-2 py-1 rounded-[4px] focus:outline-none focus:ring-0 hover:bg-gray-300 items-center gap-2"
                    onSelect={(e) => {
                      e.preventDefault(); // ngăn blur/close mặc định nếu muốn
                      navigate("/tai-khoan"); // hoặc mở modal đổi mật khẩu tuỳ bạn
                    }}
                  >
                    <SquarePen className="w-4" />
                    Đổi mật khẩu
                  </DropdownMenuItem>

                  <DropdownMenuItem
                    onSelect={(e) => {
                      e.preventDefault();
                      handleLogout();
                    }}
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
    </header>
  );
}

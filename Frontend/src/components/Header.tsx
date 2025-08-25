import { ChevronDown, LogOut, SquarePen, Eye, EyeOff } from "lucide-react";
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
import { Dialog } from "@radix-ui/react-dialog";
import {
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { changePassword } from "@/services/auth.service";
import { AxiosError } from "axios";

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

const DEFAULT_AVATAR =
  "https://graph.facebook.com/100000000000000/picture?type=large";

const PasswordSchema = z
  .object({
    currentPassword: z.string().min(6, "Mật khẩu hiện tại phải ít nhất 6 ký tự"),
    newPassword: z.string().min(6, "Mật khẩu mới phải ít nhất 6 ký tự"),
    confirmPassword: z.string().min(6, "Xác nhận mật khẩu phải ít nhất 6 ký tự"),
  })
  .refine((v) => v.newPassword === v.confirmPassword, {
    message: "Xác nhận mật khẩu không khớp",
    path: ["confirmPassword"],
  });

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
    { name: "Hội đồng", href: "/hoi-dong", hidden: role === "ADMIN" },
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

  const [openPwd, setOpenPwd] = useState(false);
  const [showCur, setShowCur] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showCfm, setShowCfm] = useState(false);

  const form = useForm<z.infer<typeof PasswordSchema>>({
    resolver: zodResolver(PasswordSchema),
    defaultValues: {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    },
  });

  const onSubmit = async (values: z.infer<typeof PasswordSchema>) => {
    try {
      const res = await changePassword({
        currentPassword: values.currentPassword,
        newPassword: values.newPassword,
      });
      const code = res?.code;
      if (code === 1000) {
        toast.success("Đổi mật khẩu thành công.");
        setOpenPwd(false);
        form.reset();
        return;
      }
    } catch (error) {
      if (error instanceof AxiosError) {
        const res = error?.response?.data;
        if (res?.code === 1007) {
          toast.error("Mật khẩu của bạn không chính xác.");
        } else if (res?.code === 1046) {
          toast.error("Bạn không thể dùng lại mật khẩu cũ.");
        } else {
          toast.error("Có lỗi xảy ra khi đổi mật khẩu." + res?.message);
        }
      }
    }
  };

  const avatarSrc = myInfo?.anhDaiDienUrl || DEFAULT_AVATAR;

  return (
    <header className="bg-white w-screen fixed top-0 shadow-sm">
      <div className="max-w-full mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-8">
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

            <img
              src={avatarSrc}
              alt="User"
              className="w-8 h-8 rounded-full cursor-pointer ml-2"
              onClick={() => navigate("/tai-khoan")}
              onError={(e) => {
                (e.currentTarget as HTMLImageElement).src = DEFAULT_AVATAR;
              }}
            />

            {/* --- DropdownMenu duy nhất (đã xoá bản trùng lặp đầu tiên) --- */}
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
                <DropdownMenuContent className=" bg-gray-100 p-5 flex flex-col space-y-2 rounded-md shadow-lg">
                  <Dialog open={openPwd} onOpenChange={setOpenPwd}>
                    <DialogTrigger asChild>
                      <DropdownMenuItem
                        onSelect={(e) => e.preventDefault()}
                        className="flex px-2 py-1 rounded-[4px] hover:bg-gray-200 cursor-pointer items-center gap-2"
                      >
                        <SquarePen className="w-4" />
                        Đổi mật khẩu
                      </DropdownMenuItem>
                    </DialogTrigger>

                    <DialogPortal>
                      <DialogOverlay className="fixed inset-0 z-40 bg-black/40" />
                      <DialogContent className="fixed z-50 left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 sm:max-w-[520px] rounded-xl bg-white p-6 shadow-lg">
                        <DialogHeader className="py-2">
                          <DialogTitle>Đổi mật khẩu</DialogTitle>
                        </DialogHeader>

                        <Form {...form}>
                          <form className="grid gap-4" onSubmit={form.handleSubmit(onSubmit)}>
                            <FormField
                              control={form.control}
                              name="currentPassword"
                              render={({ field }) => (
                                <FormItem>
                                  <FormLabel>Mật khẩu hiện tại</FormLabel>
                                  <FormControl>
                                    <div className="relative">
                                      <Input
                                        {...field}
                                        type={showCur ? "text" : "password"}
                                        autoComplete="current-password"
                                        className="h-9 text-sm pr-10 focus-visible:ring-0 focus-visible:ring-offset-0 focus:ring-0 focus:outline-none border-none bg-gray-200 shadow"
                                      />
                                      <button
                                        type="button"
                                        onClick={() => setShowCur((v) => !v)}
                                        className="absolute right-2 top-1/2 -translate-y-1/2 p-1"
                                      >
                                        {showCur ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                      </button>
                                    </div>
                                  </FormControl>
                                  <FormMessage />
                                </FormItem>
                              )}
                            />

                            <FormField
                              control={form.control}
                              name="newPassword"
                              render={({ field }) => (
                                <FormItem>
                                  <FormLabel>Mật khẩu mới</FormLabel>
                                  <FormControl>
                                    <div className="relative">
                                      <Input
                                        {...field}
                                        type={showNew ? "text" : "password"}
                                        autoComplete="new-password"
                                        className="h-9 text-sm pr-10 focus-visible:ring-0 focus-visible:ring-offset-0 focus:ring-0 focus:outline-none border-none bg-gray-200 shadow"
                                      />
                                      <button
                                        type="button"
                                        onClick={() => setShowNew((v) => !v)}
                                        className="absolute right-2 top-1/2 -translate-y-1/2 p-1"
                                      >
                                        {showNew ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                      </button>
                                    </div>
                                  </FormControl>
                                  <FormMessage />
                                </FormItem>
                              )}
                            />

                            <FormField
                              control={form.control}
                              name="confirmPassword"
                              render={({ field }) => (
                                <FormItem>
                                  <FormLabel>Xác nhận mật khẩu mới</FormLabel>
                                  <FormControl>
                                    <div className="relative">
                                      <Input
                                        {...field}
                                        type={showCfm ? "text" : "password"}
                                        autoComplete="new-password"
                                        className="h-9 text-sm pr-10 focus-visible:ring-0 focus-visible:ring-offset-0 focus:ring-0 focus:outline-none border-none bg-gray-200 shadow"
                                      />
                                      <button
                                        type="button"
                                        onClick={() => setShowCfm((v) => !v)}
                                        className="absolute right-2 top-1/2 -translate-y-1/2 p-1"
                                      >
                                        {showCfm ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                                      </button>
                                    </div>
                                  </FormControl>
                                  <FormMessage />
                                </FormItem>
                              )}
                            />

                            <DialogFooter className="gap-2">
                              <DialogClose asChild>
                                <Button
                                  type="button"
                                  className="bg-[#BFBFBF] text-white hover:bg-[#A8A8A8] focus-visible:ring-0"
                                >
                                  Trở về
                                </Button>
                              </DialogClose>
                              <Button
                                type="submit"
                                disabled={form.formState.isSubmitting}
                                className="bg-[#457B9D] text-white hover:bg-[#3D6C8B] focus-visible:ring-0"
                              >
                                {form.formState.isSubmitting ? "Đang đổi..." : "Đổi mật khẩu"}
                              </Button>
                            </DialogFooter>
                          </form>
                        </Form>
                      </DialogContent>
                    </DialogPortal>
                  </Dialog>

                  <DropdownMenuItem
                    onClick={handleLogout}
                    className="flex cursor-pointer px-2 py-1 rounded-[4px] focus:outline-none focus:ring-0 hover:bg-gray-200 items-center gap-2"
                  >
                    <LogOut className="w-4" />
                    Đăng xuất
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenuPortal>
            </DropdownMenu>
            {/* --- /DropdownMenu duy nhất --- */}
          </div>
        </div>
      </div>
    </header>
  );
}

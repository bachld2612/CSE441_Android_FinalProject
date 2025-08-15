// src/pages/Login.tsx
import { useAuthStore } from "../stores/authStore";
import { useNavigate, } from "react-router-dom";
import tluLogo from "../assets/tlu_logo 1.png";
import tluEducation from "../assets/tlu_education.png";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { toast } from "react-toastify";
import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
export default function Login() {
  const [showPassword, setShowPassword] = useState(false);
  const login = useAuthStore((s) => s.login);
  const navigate = useNavigate();

  const formSchema = z.object({
    email: z
      .string()
      .email("Email không hợp lệ")
      .min(1, "Email không được để trống"),
    password: z.string().min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
  });

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });
  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    const loginData = {
      email: values.email,
      matKhau: values.password,
    };
    const response = await login(loginData);
    if (response.code === 1000) {
      const myInfoResponse = await useAuthStore.getState().getMyInfo();
      if (myInfoResponse.code === 1000) {
        localStorage.setItem("myInfo", JSON.stringify(myInfoResponse.result));
      }
      navigate("/auth");
      toast.success("Đăng nhập thành công", {
        position: "top-right",
        autoClose: 3000,
      });

    } else {
      toast.error("Sai tài khoản hoặc mật khẩu", {
        position: "top-right",
        autoClose: 3000,
      });
    }
  };

  return (
    <div className="grid grid-cols-2 h-screen bg-[#457B9D]">
      <div className="flex flex-col items-center justify-center space-y-8 text-white">
        <img src={tluLogo} alt="TLU Logo" className="w-60" />
        <img src={tluEducation} alt="Education" className="w-100" />
      </div>

      <div className="flex items-center justify-center bg-white rounded-l-[50px] shadow-lg">
        <div className="w-1/2  flex-col justify-center items-center space-y-4">
          <h2 className="text-[35px] font-bold text-center mb-6">Đăng nhập</h2>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8 ">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        style={{
                          outline: "none",
                          boxShadow: "none",
                        }}
                        className="h-12 bg-gray-100 shadow-2xl border-none rounded-lg px-3 outline-none focus:outline-none focus:ring-0 "
                        placeholder="Vui lòng nhập email được cấp"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage className="text-[#FF0000]" />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mật khẩu</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Input
                          type={showPassword ? "text" : "password"}
                          style={{
                            outline: "none",
                            boxShadow: "none",
                          }}
                          className="h-12 bg-gray-100 shadow-2xl border-none rounded-lg px-3 outline-none focus:outline-none focus:ring-0 "
                          placeholder="********"
                          {...field}
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword((prev) => !prev)}
                          className="absolute inset-y-0 right-3 flex items-center text-gray-500 hover:text-gray-700"
                        >
                          {showPassword ? (
                            <EyeOff className="w-5 h-5" />
                          ) : (
                            <Eye className="w-5 h-5" />
                          )}
                        </button>
                      </div>
                    </FormControl>
                    <FormMessage className="text-[#FF0000]" />
                  </FormItem>
                )}
              />
              <div className="flex justify-center items-center">
                <Button
                  className="bg-[#457B9D] text-[18px] text-white py-5 text-center"
                  type="submit"
                >
                  Đăng nhập
                </Button>
              </div>
            </form>
          </Form>
          <div className="flex items-center justify-center w-full">
            <div className="text-[#FF0000] text-center w-3/4 mt-6">
              Vui lòng liên hệ với phòng đào tạo để được cấp lại tài khoản nếu mất!
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

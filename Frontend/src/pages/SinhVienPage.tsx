import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  CheckCircle,
  ChevronLeft,
  ChevronRight,
  Pencil,
  Search,
  XCircle,
} from "lucide-react";
import { useEffect, useState } from "react";
import { createSinhVien, getAllSinhVien, type SinhVien, type SinhVienCreationRequest } from "@/services/sinhVien.service";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
} from "@/components/ui/pagination";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "@/components/ui/form";
import { getAllLop, type LopResponse } from "@/services/lop.service";
import { toast } from "react-toastify";

const formSchema = z.object({
  maSV: z.string().min(5, { message: "Mã sinh viên tối thiểu 5 ký tự" }),
  hoTen: z.string().min(2, { message: "Họ tên không được bỏ trống" }),
  soDienThoai: z
    .string()
    .regex(/^[0-9]{10}$/, { message: "SĐT phải đủ 10 số" }),
  email: z.string().email({ message: "Email không hợp lệ" }),
  matKhau: z.string().min(6, { message: "Mật khẩu ít nhất 6 ký tự" }),
  lopId: z.string().nonempty({ message: "Bạn phải chọn lớp" }),
});

export default function SinhVienPage() {
  const [students, setStudents] = useState<SinhVien[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [openCreateDialog, setOpenCreateDialog] = useState(false);

  const [lopList, setLopList] = useState<LopResponse[]>([]);
  const [lopSearch, setLopSearch] = useState("");
  const [filteredLop, setFilteredLop] = useState<LopResponse[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      maSV: "",
      hoTen: "",
      soDienThoai: "",
      email: "",
      matKhau: "",
      lopId: "",
    },
  });

  const onSubmit = async (data: z.infer<typeof formSchema>) => {
    console.log("Form submit:", data);
    const sinhVienData: SinhVienCreationRequest = {
      maSV: data.maSV,
      hoTen: data.hoTen,
      soDienThoai: data.soDienThoai,
      email: data.email,
      matKhau: data.matKhau,
      lopId: parseInt(data.lopId, 10),
    }
    const response = await createSinhVien(sinhVienData);
    if(response.code === 1000) {
      toast.success("Thêm sinh viên thành công", {
        position: "top-right",
        autoClose: 3000,
      });
      loadData();
      setOpenCreateDialog(false);
      form.reset();
    }else{
      toast.error("Thêm sinh viên thất bại: " + response.message, {
        position: "top-right",
        autoClose: 3000,
      });
    }
  };

  const handleLopChange = (value: string) => {
    setLopSearch(value);
    setShowSuggestions(true);
    setFilteredLop(
      lopList
        .filter((lop) =>
          (lop.tenLop ?? "").toLowerCase().includes(value.toLowerCase())
        )
        .slice(0, 3)
    );
  };
  useEffect(() => {
    const fetchLop = async () => {
      try {
        const res = await getAllLop();
        console.log("LopService - getAllLop response:", res);
        if (res.result) {
          setLopList(res.result);
        }
      } catch (err) {
        console.error("Lỗi khi load lớp:", err);
      }
    };
    fetchLop();
  }, []);

  useEffect(() => {
    loadData();
  }, [page]);

  const loadData = async () => {
    try {
      const data = await getAllSinhVien({
        page,
        size,
        sort: "maSV,asc",
      });
      setStudents(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error("Lỗi khi load sinh viên:", err);
    }
  };

  return (
    <div>
      {/* Breadcrumb */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="/students">
              Sinh viên
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-4">
        Quản lý sinh viên
      </h1>

      <div className="flex justify-between items-center">
        <div className="flex items-center gap-2">
          {/* Dialog thêm sinh viên */}
          <Dialog open={openCreateDialog} onOpenChange={setOpenCreateDialog}>
            <DialogTrigger asChild>
              <Button variant="outline">Thêm sinh viên</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px] bg-white border-none rounded-lg shadow-lg">
              <DialogHeader>
                <DialogTitle className="text-center">
                  Thêm sinh viên
                </DialogTitle>
              </DialogHeader>

              <Form {...form}>
                <form
                  onSubmit={form.handleSubmit(onSubmit)}
                  className="grid gap-4 mt-2"
                >
                  {/* Mã SV */}
                  <FormField
                    control={form.control}
                    name="maSV"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Mã sinh viên</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="2251172245"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500" />
                      </FormItem>
                    )}
                  />
                  {/* Họ tên */}
                  <FormField
                    control={form.control}
                    name="hoTen"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Họ tên</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="Nguyễn Văn A"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500" />
                      </FormItem>
                    )}
                  />
                  {/* SĐT */}
                  <FormField
                    control={form.control}
                    name="soDienThoai"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Số điện thoại</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="0338518849"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500" />
                      </FormItem>
                    )}
                  />
                  {/* Email */}
                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Email</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="example@email.com"
                            type="email"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500" />
                      </FormItem>
                    )}
                  />
                  {/* Mật khẩu */}
                  <FormField
                    control={form.control}
                    name="matKhau"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Mật khẩu</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="********"
                            type="password"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500" />
                      </FormItem>
                    )}
                  />
                  {/* Lớp */}
                  <FormField
                    control={form.control}
                    name="lopId"
                    render={() => (
                      <FormItem className="relative">
                        <FormLabel>Lớp</FormLabel>
                        <FormControl>
                          <Input
                            value={lopSearch}
                            placeholder="Nhập tên lớp..."
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                            onChange={(e) => handleLopChange(e.target.value)}
                          />
                        </FormControl>
                        {showSuggestions && lopSearch && (
                          <div className="absolute top-full mt-1 w-full bg-white border border-gray-300 rounded shadow-md z-10">
                            {filteredLop.length > 0 ? (
                              filteredLop.map((lop) => (
                                <div
                                  key={lop.id}
                                  className="px-3 py-2 hover:bg-gray-100 cursor-pointer"
                                  onClick={() => {
                                    form.setValue("lopId", lop.id.toString());
                                    setLopSearch(lop.tenLop ?? "");
                                    setShowSuggestions(false);
                                  }}
                                >
                                  {lop.tenLop}
                                </div>
                              ))
                            ) : (
                              <div className="px-3 py-2 text-gray-500">
                                Không tìm thấy lớp
                              </div>
                            )}
                          </div>
                        )}
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <DialogFooter className="mt-4 flex gap-2">
                    <DialogClose asChild>
                      <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                        Trở về
                      </Button>
                    </DialogClose>
                    <Button
                      type="submit"
                      className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                    >
                      Thêm sinh viên
                    </Button>
                  </DialogFooter>
                </form>
              </Form>
            </DialogContent>
          </Dialog>

          {/* Dialog import */}
          <Dialog>
            <DialogTrigger asChild>
              <Button variant="outline">Import sinh viên</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px] border-none ">
              <DialogHeader>
                <DialogTitle className="text-center">
                  Import sinh viên
                </DialogTitle>
              </DialogHeader>
              <DialogDescription className="text-center">
                Upload file Excel chứa thông tin sinh viên.
              </DialogDescription>
              <div className="mt-4">
                <a className="text-blue-600 underline" href="">
                  Tải file mẫu
                </a>
                <div className="grid gap-3 mt-4">
                  <Input id="file-upload" name="file-upload" type="file" />
                </div>
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button className="border-none bg-[#BFBFBF] text-white">
                    Trở về
                  </Button>
                </DialogClose>
                <Button type="submit">Thêm sinh viên</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {/* Ô search */}
        <form className="flex items-center gap-1">
          <Input
            type="text"
            placeholder="Tìm kiếm sinh viên..."
            className="w-[300px] border-gray-300 h-10"
          />
          <Button
            type="submit"
            className="h-10 border-gray-300"
            variant="outline"
          >
            <Search />
          </Button>
        </form>
      </div>

      {/* Table */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">
              Tài khoản
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Mật khẩu
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Trạng thái
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Hành động
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {students.map((s) => (
            <TableRow
              key={s.maSV}
              className="hover:bg-gray-50 transition-colors"
            >
              <TableCell className="text-center border border-gray-300 font-medium">
                {s.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.kichHoat ? (
                  <span className="text-green-600 font-semibold">
                    Đã kích hoạt
                  </span>
                ) : (
                  <span className="text-red-600 font-semibold">
                    Chưa kích hoạt
                  </span>
                )}
              </TableCell>
              <TableCell className="border border-gray-300">
                <div className="flex justify-center gap-3">
                  <Pencil className="w-5 h-5 text-yellow-500 cursor-pointer" />
                  {s.kichHoat ? (
                    <XCircle className="w-5 h-5 text-gray-500 cursor-pointer" />
                  ) : (
                    <CheckCircle className="w-5 h-5 text-green-500 cursor-pointer" />
                  )}
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Pagination */}
      <div className="flex justify-end mx-auto mt-6">
        <div className="flex justify-center mt-6">
          <Pagination>
            <PaginationContent className="flex items-center gap-2">
              {/* Prev */}
              <PaginationItem>
                <button
                  onClick={(e) => {
                    e.preventDefault();
                    if (page > 0) setPage(page - 1);
                  }}
                  className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
                    page === 0
                      ? "pointer-events-none opacity-50"
                      : "hover:bg-gray-200"
                  }`}
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
              </PaginationItem>

              {/* Pages */}
              {Array.from({ length: totalPages }, (_, i) => (
                <PaginationItem key={i}>
                  <button
                    onClick={(e) => {
                      e.preventDefault();
                      setPage(i);
                    }}
                    className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 ${
                      page === i
                        ? "bg-[#2F80ED] text-white font-semibold"
                        : "bg-gray-100 hover:bg-gray-200"
                    }`}
                  >
                    {i + 1}
                  </button>
                </PaginationItem>
              ))}

              {/* Next */}
              <PaginationItem>
                <button
                  onClick={(e) => {
                    e.preventDefault();
                    if (page + 1 < totalPages) setPage(page + 1);
                  }}
                  className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
                    page + 1 >= totalPages
                      ? "pointer-events-none opacity-50"
                      : "hover:bg-gray-200"
                  }`}
                >
                  <ChevronRight className="w-4 h-4 " />
                </button>
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      </div>
    </div>
  );
}

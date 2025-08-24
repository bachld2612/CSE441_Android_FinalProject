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
import {
  changeSinhVienStatus,
  createSinhVien,
  findSinhVienByInfo,
  getAllSinhVien,
  importSinhVien,
  updateSinhVien,
  type SinhVien,
  type SinhVienCreationRequest,
} from "@/services/sinhVien.service";
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
import { AxiosError } from "axios";
import { Label } from "@/components/ui/label";

const formSchema = z.object({
  maSV: z.string().min(1, { message: "Mã sinh viên không được bỏ trống" }),
  hoTen: z.string().min(2, { message: "Họ tên không được bỏ trống" }),
  soDienThoai: z
    .string()
    .regex(/^[0-9]{10}$/, { message: "SĐT phải đủ 10 số" }),
  email: z.string().email({ message: "Email không hợp lệ" }),
  matKhau: z.string().min(6, { message: "Mật khẩu ít nhất 6 ký tự" }),
  lopId: z.string().nonempty({ message: "Bạn phải chọn lớp" }),
});

const updateSchema = z.object({
  hoTen: z.string().min(2, { message: "Họ tên không được bỏ trống" }),
  soDienThoai: z
    .string()
    .regex(/^[0-9]{10}$/, { message: "SĐT phải đủ 10 số" }),
  email: z.string().email({ message: "Email không hợp lệ" }),
  matKhau: z.string().optional(), // có thể để trống
  lopId: z.string().nonempty({ message: "Bạn phải chọn lớp" }),
});

export default function SinhVienPage() {
  const [lopSearchUpdate, setLopSearchUpdate] = useState("");
  const [filteredLopUpdate, setFilteredLopUpdate] = useState<LopResponse[]>([]);
  const [showSuggestionsUpdate, setShowSuggestionsUpdate] = useState(false);
  const [role, setRole] = useState<string | null>(null);
  const [students, setStudents] = useState<SinhVien[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [openCreateDialog, setOpenCreateDialog] = useState(false);

  const [lopList, setLopList] = useState<LopResponse[]>([]);
  const [lopSearch, setLopSearch] = useState("");
  const [filteredLop, setFilteredLop] = useState<LopResponse[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [isShowImportDialog, setIsShowImportDialog] = useState(false);
  const [searchInfo, setSearchInfo] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedMaSV, setSelectedMaSV] = useState<string | null>(null);
  const [selectedKichHoat, setSelectedKichHoat] = useState<boolean>(false);

  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editingSinhVien, setEditingSinhVien] = useState<SinhVien | null>(null);

  const [file, setFile] = useState<File | null>(null);

  // ===================== ADD: cấu hình số cột để dùng cho colSpan =====================
  const showActionCol = role === "TRO_LY_KHOA"; // có cột Hành động hay không
  const baseCols = 6; // Email, Mã SV, Họ Tên, Lớp, SĐT, Trạng thái
  const totalCols = baseCols + (showActionCol ? 1 : 0);
  // ====================================================================================

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

  const updateForm = useForm<z.infer<typeof updateSchema>>({
    resolver: zodResolver(updateSchema),
    defaultValues: {
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
    };
    try {
      const response = await createSinhVien(sinhVienData);
      if (response.code === 1000) {
        toast.success("Thêm sinh viên thành công", {
          position: "top-right",
          autoClose: 3000,
        });
        loadData();
        setOpenCreateDialog(false);
        form.reset();
      }
    } catch (error) {
      console.error("SinhVienPage - onSubmit error:", error);
      if (error instanceof AxiosError) {
        if (error.response?.data?.code === 1024) {
          const errorMessage = "Email đã được sử dụng";
          toast.error(`Thêm sinh viên thất bại: ${errorMessage}`, {
            position: "top-right",
            autoClose: 3000,
          });
        } else if (error.response?.data?.code === 1025) {
          const errorMessage = "Mã sinh viên đã tồn tại";
          toast.error(`Thêm sinh viên thất bại: ${errorMessage}`, {
            position: "top-right",
            autoClose: 3000,
          });
        } else {
          toast.error(`Thêm sinh viên thất bại: ${error.message}`, {
            position: "top-right",
            autoClose: 3000,
          });
        }
      }
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

  const handleLopChangeUpdate = (value: string) => {
    setLopSearchUpdate(value);
    setShowSuggestionsUpdate(true);
    setFilteredLopUpdate(
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
    const currentRole = localStorage.getItem("myInfo");
    if (currentRole) {
      const parsedRole = JSON.parse(currentRole).role;
      setRole(parsedRole);
    }
    if (isSearching) {
      // nếu đang ở chế độ search thì gọi lại API search
      const fetchSearch = async () => {
        const res = await findSinhVienByInfo(searchTerm, {
          page,
          size,
          sort: "maSV,asc",
        });
        setStudents(res.content);
        setTotalPages(res.totalPages);
      };
      fetchSearch();
    } else {
      // ngược lại load all
      loadData();
    }
  }, [page, isSearching]);
  const loadData = async () => {
    try {
      const data = await getAllSinhVien({
        page,
        size,
        sort: "lop.tenLop,asc",
      });
      setStudents(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error("Lỗi khi load sinh viên:", err);
    }
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchInfo.trim()) {
      setIsSearching(false);
      setPage(0);
      loadData();
      return;
    }

    try {
      const res = await findSinhVienByInfo(searchInfo, {
        page: 0,
        size: size,
        sort: "maSV,asc",
      });
      setStudents(res.content);
      setTotalPages(res.totalPages);
      setPage(0);
      setSearchTerm(searchInfo); // lưu lại từ khoá
      setIsSearching(true); // bật search mode
    } catch (error) {
      console.error("SinhVienPage - handleSearch error:", error);
    }
  };

  const handleImportSinhVien = async () => {
    if (!file) {
      toast.error("Vui lòng chọn file trước khi import", {
        position: "top-right",
      });
      return;
    }
    try {
      const response = await importSinhVien(file);
      if (response.code === 1000) {
        toast.success("Import sinh viên thành công", {
          position: "top-right",
          autoClose: 3000,
        });
        loadData();
        setIsShowImportDialog(false);
        setFile(null);
      }
    } catch (error) {
      console.error("SinhVienPage - handleImportSinhVien error:", error);
      if (error instanceof AxiosError) {
        toast.error(`Import sinh viên thất bại: ${error.message}`, {
          position: "top-right",
          autoClose: 3000,
        });
      } else {
        toast.error(`Import sinh viên thất bại: ${error}`, {
          position: "top-right",
          autoClose: 3000,
        });
      }
    }
  };

  const handleChangeStatus = async () => {
    if (!selectedMaSV) return;

    try {
      const response = await changeSinhVienStatus(selectedMaSV);
      if (response.code === 1000) {
        toast.success(
          selectedKichHoat
            ? "Khóa tài khoản thành công"
            : "Kích hoạt tài khoản thành công",
          { position: "top-right", autoClose: 3000 }
        );

        // Reload lại data (check search mode hay load all)
        if (isSearching) {
          const res = await findSinhVienByInfo(searchTerm, {
            page,
            size,
            sort: "maSV,asc",
          });
          setStudents(res.content);
          setTotalPages(res.totalPages);
        } else {
          loadData();
        }

        // Đóng dialog
        setSelectedMaSV(null);
      }
    } catch (error) {
      console.error("SinhVienPage - handleChangeStatus error:", error);
      toast.error(`Không tìm thấy sinh viên`, {
        position: "top-right",
        autoClose: 3000,
      });
    }
  };

  const handleUpdate = async (data: z.infer<typeof updateSchema>) => {
    if (!editingSinhVien) return;

    const sinhVienUpdate: SinhVienCreationRequest = {
      maSV: editingSinhVien.maSV,
      hoTen: data.hoTen,
      soDienThoai: data.soDienThoai,
      email: data.email,
      matKhau: data.matKhau || "",
      lopId: parseInt(data.lopId, 10),
    };

    try {
      const res = await updateSinhVien(editingSinhVien.maSV, sinhVienUpdate);
      if (res.code === 1000) {
        toast.success("Cập nhật sinh viên thành công", { autoClose: 3000 });
        loadData();
        setOpenEditDialog(false);
        setEditingSinhVien(null);
      }
    } catch (error) {
      console.error("SinhVienPage - onSubmit error:", error);
      if (error instanceof AxiosError) {
        if (error.response?.data?.code === 1024) {
          const errorMessage = "Email đã được sử dụng";
          toast.error(`Thêm sinh viên thất bại: ${errorMessage}`, {
            position: "top-right",
            autoClose: 3000,
          });
        } else if (error.response?.data?.code === 1025) {
          const errorMessage = "Mã sinh viên đã tồn tại";
          toast.error(`Thêm sinh viên thất bại: ${errorMessage}`, {
            position: "top-right",
            autoClose: 3000,
          });
        } else {
          toast.error(`Thêm sinh viên thất bại: ${error.message}`, {
            position: "top-right",
            autoClose: 3000,
          });
        }
      }
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
            <BreadcrumbLink className="font-bold" href="/sinh-vien">
              Sinh viên
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-4">
        Quản lý sinh viên
      </h1>

      <div className="flex justify-between items-center">
        {role === "TRO_LY_KHOA" && (
          <div className="flex items-center gap-2">
            {/* Dialog thêm sinh viên */}
            <Dialog open={openCreateDialog} onOpenChange={setOpenCreateDialog}>
              <DialogTrigger asChild>
                <Button
                  className="border-none cursor-pointer bg-[#457B9D] text-white text-center"
                  variant="outline"
                >
                  Thêm sinh viên
                </Button>
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

            <Dialog
              open={isShowImportDialog}
              onOpenChange={setIsShowImportDialog}
            >
              <DialogTrigger asChild>
                <Button
                  className="border-none cursor-pointer bg-[#457B9D] text-white text-center"
                  variant="outline"
                >
                  Import sinh viên
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[425px] bg-white border-none rounded-lg shadow-lg">
                <DialogHeader>
                  <DialogTitle className="text-center">
                    Import sinh viên
                  </DialogTitle>
                  <DialogDescription className="text-center">
                    Upload file Excel chứa thông tin sinh viên.
                  </DialogDescription>
                </DialogHeader>

                <div className="mt-4">
                  <a
                    className="text-blue-600 underline"
                    download="/assets/sinhvien.xlsx"
                    href="/assets/sinhvien.xlsx"
                  >
                    Tải file mẫu
                  </a>
                  <div className="grid gap-3 mt-4">
                    <Label htmlFor="file-upload">Chọn file</Label>
                    <Input
                      id="file-upload"
                      name="file-upload"
                      type="file"
                      accept=".xlsx,.xlsm"
                      onChange={(e) => {
                        if (e.target.files && e.target.files[0]) {
                          setFile(e.target.files[0]);
                        }
                      }}
                      className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                    />
                  </div>
                </div>

                <DialogFooter className="mt-4 flex gap-2">
                  <DialogClose asChild>
                    <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                      Trở về
                    </Button>
                  </DialogClose>
                  <Button
                    type="button"
                    onClick={handleImportSinhVien}
                    className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                  >
                    Thêm sinh viên
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        )}

        {/* Ô search */}
        <form onSubmit={handleSearch} className="flex items-center gap-1">
          <Input
            type="text"
            placeholder="Tìm kiếm sinh viên..."
            className="w-[300px] border-gray-300 h-10 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
            value={searchInfo}
            onChange={(e) => setSearchInfo(e.target.value)}
          />
          <Button
            type="submit"
            className="h-10 border-gray-300"
            variant="outline"
          >
            <Search className="text-gray-500" />
          </Button>
        </form>
      </div>

      {/* Table */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">
              Email
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Mã Sinh Viên
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Họ Tên
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Lớp
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Số Điện Thoại
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Trạng thái
            </TableHead>
            {role === "TRO_LY_KHOA" && (
              <TableHead className="text-center font-semibold border border-gray-300">
                Hành động
              </TableHead>
            )}
          </TableRow>
        </TableHeader>
        <TableBody>
          {/* ===================== ADD: Hiển thị khi không có dữ liệu ===================== */}
          {students.length === 0 && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={totalCols}
              >
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}
          {/* ============================================================================ */}

          {students.map((s) => (
            <TableRow
              key={s.maSV}
              className="hover:bg-gray-50 transition-colors"
            >
              <TableCell className="text-center border border-gray-300">
                {s.email}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.hoTen}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.tenLop ?? "Chưa có lớp"}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {s.soDienThoai}
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
              {role === "TRO_LY_KHOA" && (
                <TableCell className="border border-gray-300">
                  <div className="flex justify-center gap-3">
                    <Pencil
                      className="w-5 h-5 text-yellow-500 cursor-pointer"
                      onClick={() => {
                        setEditingSinhVien(s);
                        setOpenEditDialog(true);

                        // Reset form
                        updateForm.reset({
                          hoTen: s.hoTen,
                          soDienThoai: s.soDienThoai,
                          email: s.email,
                          matKhau: "",
                          lopId: "", // sẽ set ở dưới
                        });

                        // Hiển thị tên lớp cũ trong input
                        setLopSearchUpdate(s.tenLop ?? "");

                        // Nếu tìm thấy lopId từ lopList thì gán vào form
                        const lopFound = lopList.find(
                          (lop) => lop.tenLop === s.tenLop
                        );
                        if (lopFound) {
                          updateForm.setValue("lopId", lopFound.id.toString());
                        }
                      }}
                    />

                    {s.kichHoat ? (
                      <XCircle
                        className="w-5 h-5 text-gray-500 cursor-pointer"
                        onClick={() => {
                          setSelectedMaSV(s.maSV);
                          setSelectedKichHoat(s.kichHoat);
                        }}
                      />
                    ) : (
                      <CheckCircle
                        className="w-5 h-5 text-green-500 cursor-pointer"
                        onClick={() => {
                          setSelectedMaSV(s.maSV);
                          setSelectedKichHoat(s.kichHoat);
                        }}
                      />
                    )}
                  </div>
                </TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>

        {/* Update Dialog */}
        <Dialog open={openEditDialog} onOpenChange={setOpenEditDialog}>
          <DialogContent className="sm:max-w-[425px] bg-white border-none rounded-lg shadow-lg">
            <DialogHeader>
              <DialogTitle className="text-center">Sửa sinh viên</DialogTitle>
            </DialogHeader>

            {editingSinhVien && (
              <Form {...updateForm}>
                <form
                  onSubmit={updateForm.handleSubmit(handleUpdate)}
                  className="grid gap-4 mt-2"
                >
                  {/* Email */}
                  <FormField
                    control={updateForm.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Email</FormLabel>
                        <FormControl>
                          <Input
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Họ tên */}
                  <FormField
                    control={updateForm.control}
                    name="hoTen"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Họ tên</FormLabel>
                        <FormControl>
                          <Input
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* SĐT */}
                  <FormField
                    control={updateForm.control}
                    name="soDienThoai"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Số điện thoại</FormLabel>
                        <FormControl>
                          <Input
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Mật khẩu */}
                  <FormField
                    control={updateForm.control}
                    name="matKhau"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Mật khẩu mới</FormLabel>
                        <FormControl>
                          <Input
                            type="password"
                            {...field}
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {/* Lớp (autocomplete giống thêm) */}
                  <FormField
                    control={updateForm.control}
                    name="lopId"
                    render={() => (
                      <FormItem className="relative">
                        <FormLabel>Lớp</FormLabel>
                        <FormControl>
                          <Input
                            value={lopSearchUpdate}
                            onChange={(e) =>
                              handleLopChangeUpdate(e.target.value)
                            }
                            className="border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0 focus:outline-none"
                            placeholder="Nhập tên lớp..."
                          />
                        </FormControl>

                        {showSuggestionsUpdate && lopSearchUpdate && (
                          <div className="absolute top-full mt-1 w-full bg-white border border-gray-300 rounded shadow-md z-10">
                            {filteredLopUpdate.length > 0 ? (
                              filteredLopUpdate.map((lop) => (
                                <div
                                  key={lop.id}
                                  className="px-3 py-2 hover:bg-gray-100 cursor-pointer"
                                  onClick={() => {
                                    updateForm.setValue(
                                      "lopId",
                                      lop.id.toString()
                                    );
                                    setLopSearchUpdate(lop.tenLop ?? "");
                                    setShowSuggestionsUpdate(false);
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
                      </FormItem>
                    )}
                  />

                  <DialogFooter className="mt-4 flex gap-2">
                    <DialogClose asChild>
                      <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                        Hủy
                      </Button>
                    </DialogClose>
                    <Button
                      type="submit"
                      className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                    >
                      Lưu thay đổi
                    </Button>
                  </DialogFooter>
                </form>
              </Form>
            )}
          </DialogContent>
        </Dialog>

        {/* Dialog đổi trạng thái */}
        <Dialog
          open={!!selectedMaSV}
          onOpenChange={(open) => {
            if (!open) setSelectedMaSV(null);
          }}
        >
          <DialogContent className="sm:max-w-[425px] bg-white border-none rounded-lg shadow-lg">
            <DialogHeader>
              <DialogTitle className="text-center">
                {selectedKichHoat ? "Khóa tài khoản" : "Kích hoạt tài khoản"}
              </DialogTitle>
            </DialogHeader>
            <DialogDescription className="text-center">
              {selectedKichHoat
                ? "Bạn có chắc chắn muốn khóa tài khoản này?"
                : "Bạn có chắc chắn muốn kích hoạt tài khoản này?"}
            </DialogDescription>
            <DialogFooter className="flex gap-2">
              <DialogClose asChild>
                <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                  Hủy
                </Button>
              </DialogClose>
              <Button
                type="button"
                onClick={handleChangeStatus}
                className={`${
                  selectedKichHoat
                    ? "bg-red-400 hover:bg-red-500"
                    : "bg-[#457B9D] hover:bg-[#35607a]"
                } text-white`}
              >
                {selectedKichHoat ? "Khóa" : "Kích hoạt"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
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

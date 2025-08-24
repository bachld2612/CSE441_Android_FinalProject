import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useEffect, useState } from "react";
import {
  getSinhVienByMaSV,
  getSinhVienWithoutDeTai,
  type SinhVienInfoResponse,
  type SinhVienWihtoutDeTai,
} from "@/services/sinhVien.service";
import { AxiosError } from "axios";
import { toast } from "react-toastify";
import { getAllBoMon, type BoMonResponse } from "@/services/bo-mon.service";
import {
  getGiangVienByBoMonAndSoLuongDeTai,
  type GiangVienInfoResponse,
} from "@/services/giang-vien.service";
import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { DialogDescription } from "@radix-ui/react-dialog";
import {
  addGiangVienHuongDan,
  type DeTaiGiangVienHuongDanRequest,
} from "@/services/deTai.service";
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb";
import { downloadFile } from "@/lib/downloadFile";

const formSchema = z.object({
  boMonId: z.string().nonempty("Vui lòng chọn bộ môn"),
  maGV: z.string().nonempty("Vui lòng chọn giảng viên"),
});

type FormValues = z.infer<typeof formSchema>;

export default function DangKiGiangVienHuongDan() {
  const [sinhVienWithoutDeTai, setSinhVienWithoutDeTai] = useState<
    SinhVienWihtoutDeTai[]
  >([]);
  const [reloadData, setReloadData] = useState(false);
  const [boMon, setBoMon] = useState<BoMonResponse[]>([]);
  const [giangVien, setGiangVien] = useState<GiangVienInfoResponse[]>([]);

  // dialog state
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedMaSV, setSelectedMaSV] = useState<string | null>(null);
  const [sinhVienDetail, setSinhVienDetail] = useState<SinhVienInfoResponse>(
    {} as SinhVienInfoResponse
  );

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      boMonId: "",
      maGV: "",
    },
  });

  const onSubmit = async (values: FormValues) => {
    console.log("Form submit:", values);
    console.log("Selected MaSV:", selectedMaSV);
    // TODO: gọi API backend ở đây
    try {
      const request: DeTaiGiangVienHuongDanRequest = {
        maSV: selectedMaSV ?? "",
        maGV: values.maGV,
      };
      const response = await addGiangVienHuongDan(request);
      if (response.code === 1000 && response.result?.success) {
        toast.success("Thêm giảng viên hướng dẫn thành công");
        setDialogOpen(false);
        handleReload();
      }
    } catch (error) {
      if (error instanceof AxiosError) {
        toast.error(`Lỗi: ${error.response?.data.message || error.message}`);
      } else {
        toast.error("Đã xảy ra lỗi khi thêm giảng viên hướng dẫn");
      }
    }
  };

  const fetchSinhVienWithoutDeTai = async () => {
    try {
      const response = await getSinhVienWithoutDeTai();
      setSinhVienWithoutDeTai(response.result);
      console.log("Sinh viên không có đề tài:", response.result);
    } catch (error) {
      if (error instanceof AxiosError) {
        toast.error(`Lỗi: ${error.response?.data.message || error.message}`);
      }
    }
  };

  useEffect(() => {
    fetchSinhVienWithoutDeTai();
  }, [reloadData]);

  const handleOpenChange = async (open: boolean) => {
    setDialogOpen(open);
    if (open && selectedMaSV) {
      try {
        const response = await getSinhVienByMaSV(selectedMaSV);
        setSinhVienDetail(response.result);
        console.log("Chi tiết sinh viên:", response.result);
        const listBoMon = await getAllBoMon();
        setBoMon(listBoMon.result ?? []);
        console.log("Danh sách bộ môn:", listBoMon.result);
      } catch (error) {
        if (error instanceof AxiosError) {
          toast.error(`Lỗi: Không thể lấy thông tin cụ thể của sinh viên`);
        }
      }
    }
  };

  useEffect(() => {
    handleOpenChange(dialogOpen);
  }, [selectedMaSV]);

  const handleReload = () => {
    setReloadData((prev) => !prev);
  };

  const handleBoMonChange = async (value: string) => {
    console.log("Bộ môn đã chọn:", value);
    try {
      const response = await getGiangVienByBoMonAndSoLuongDeTai(
        parseInt(value)
      );
      if (response.code === 1000) {
        console.log("Danh sách giảng viên:", response.result);
        setGiangVien(response.result ?? []);
      }
    } catch (error) {
      if (error instanceof AxiosError) {
        toast.error(`Lỗi: Không thể lấy danh sách giảng viên`);
      }
    }
  };
  return (
    <div className="flex flex-col  justify-center ">
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="/sinh-vien/gvhd">
              Đăng ký giảng viên hướng dẫn
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>


      <h1 className="text-3xl mt-10 font-bold mb-4 text-center">Đăng ký giảng viên hướng dẫn</h1>

      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">
              Mã Sinh Viên
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Giảng Viên
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Hành động
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {sinhVienWithoutDeTai.map((row) => (
            <TableRow
              key={row.maSV}
              className="hover:bg-gray-50 transition-colors"
            >
              <TableCell className="text-center border border-gray-300">
                {row.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {row.hoTen ?? (
                  <span className="text-gray-400 italic">Chưa có</span>
                )}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                <Button
                  className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                  onClick={() => {
                    setSelectedMaSV(row.maSV); // set maSV cho row này
                    setDialogOpen(true); // mở dialog
                  }}
                >
                  Thêm giảng viên hướng dẫn
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* 1 dialog duy nhất */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="sm:max-w-[425px] bg-white border-none rounded-lg shadow-lg">
          <DialogHeader>
            <DialogTitle className="text-center">
              Thêm giảng viên hướng dẫn
            </DialogTitle>
            <DialogDescription className="text-center text-sm text-gray-600">
              Vui chọn bộ môn để lọc ra các giảng viên phù hợp.
            </DialogDescription>
          </DialogHeader>

          {/* Thông tin sinh viên */}
          <div className="p-3 border rounded bg-gray-50 text-sm mb-4">
            <p>
              <span className="font-semibold">Mã SV:</span>{" "}
              {sinhVienDetail.maSV}
            </p>
            <p>
              <span className="font-semibold">Họ tên:</span>{" "}
              {sinhVienDetail.hoTen ?? "Chưa có"}
            </p>
            <p>
              <span className="font-semibold">Lớp:</span>{" "}
              {sinhVienDetail.tenLop ?? "Chưa có"}
            </p>
            <p>
              <span className="font-semibold">Ngành:</span>{" "}
              {sinhVienDetail.tenNganh ?? "Chưa có"}
            </p>
            <p>
              <span className="font-semibold">Khoa:</span>{" "}
              {sinhVienDetail.tenKhoa ?? "Chưa có"}
            </p>
            <p>
              <span className="font-semibold">CV:</span>{" "}
              {(sinhVienDetail.cvUrl && (
                <span className="text-blue-700 underline" onClick={() => downloadFile(sinhVienDetail.cvUrl!, `${sinhVienDetail.maSV}_CV.pdf`)}>
                  CV sinh viên
                </span>
              )) ||
                "Chưa có"}
            </p>
          </div>

          {/* Form chọn bộ môn & giảng viên */}
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              {/* Bộ môn */}
              <FormField
                control={form.control}
                name="boMonId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Bộ môn</FormLabel>
                    <Select
                      onValueChange={(value) => {
                        field.onChange(value);
                        handleBoMonChange(value);
                      }}
                      value={field.value}
                    >
                      <FormControl>
                        <SelectTrigger className="w-full border-gray-300">
                          <SelectValue placeholder="Chọn bộ môn" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent className="bg-white">
                        {boMon.map((bm) => (
                          <SelectItem key={bm.id} value={bm.id.toString()}>
                            {bm.tenBoMon}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Giảng viên */}
              <FormField
                control={form.control}
                name="maGV"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Giảng viên</FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger className="w-full border-gray-300">
                          <SelectValue placeholder="Chọn giảng viên" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent className="bg-white">
                        {giangVien.map((gv) => (
                          <SelectItem key={gv.maGV} value={gv.maGV ?? ""}>
                            {gv.hocHam} {gv.hocVi} {gv.hoTen} - đã nhận{" "}
                            {gv.soLuongDeTai ?? 0} đề tài
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <DialogFooter className="mt-6 flex gap-2">
                <DialogClose asChild>
                  <Button
                    type="button"
                    className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]"
                  >
                    Hủy
                  </Button>
                </DialogClose>
                <Button
                  type="submit"
                  className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                >
                  Lưu
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

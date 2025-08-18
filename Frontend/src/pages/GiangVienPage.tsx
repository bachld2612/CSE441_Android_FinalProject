import { useEffect, useMemo, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Dialog, DialogClose, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle, DialogTrigger,
} from "@/components/ui/dialog";
import { ChevronLeft, ChevronRight, Search, UserPlus, Upload, ShieldCheck } from "lucide-react";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  getGiangVienPage,
  type TPage as PageType,
  type TGiangVienResponse as GiangVien,
  createGiangVien,
  importGiangVien,
  createTroLyKhoa,
} from "@/services/giang-vien.service";
import {
  getBoMonWithTBMPage,
  type BoMonWithTruongBoMonResponse,
} from "@/services/bo-mon.service";
import { toast } from "react-toastify";
import { AxiosError } from "axios";

// ====== Form schema (tạo giảng viên) — bám gần regex BE ======
const createSchema = z.object({
  maGV: z.string().regex(/^[0-9]{10}$/, "Mã GV phải gồm 10 chữ số"),
  hoTen: z.string().min(2, "Họ tên không được bỏ trống"),
  soDienThoai: z.string().regex(/^[0-9]{10}$/, "Số điện thoại phải đủ 10 số"),
  email: z.string().email("Email không hợp lệ"),
  matKhau: z.string().min(6, "Mật khẩu tối thiểu 6 ký tự"),
  hocVi: z.string().optional(),
  hocHam: z.string().optional(),
  boMonId: z.string().nonempty("Bạn phải chọn bộ môn"),
});

type CreateFormValues = z.infer<typeof createSchema>;

export default function GiangVienPage() {
  // ====== Role từ localStorage.myInfo.role ======
  const [role, setRole] = useState<string | null>(null);

  // ====== Dữ liệu GV (server-side) ======
  const [data, setData] = useState<GiangVien[]>([]);
  const [page, setPage] = useState(0); // 0-based
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // sort server-side
  const [sort, setSort] =
    useState<"maGV,asc" | "maGV,desc" | "hoTen,asc" | "hoTen,desc">("maGV,asc");

  // search client-side
  const [q, setQ] = useState("");

  // ====== Bộ môn map (id -> tên) ======
  const [boMonMap, setBoMonMap] = useState<Record<number, string>>({});
  const [boMonOptions, setBoMonOptions] = useState<{ id: number; tenBoMon: string }[]>([]);
  const [loadingBoMon, setLoadingBoMon] = useState(false);

  // ====== Dialogs: Create + Import ======
  const [openCreate, setOpenCreate] = useState(false);
  const [openImport, setOpenImport] = useState(false);
  const [file, setFile] = useState<File | null>(null);

  // ====== Form tạo giảng viên ======
  const createForm = useForm<CreateFormValues>({
    resolver: zodResolver(createSchema),
    defaultValues: {
      maGV: "",
      hoTen: "",
      soDienThoai: "",
      email: "",
      matKhau: "",
      hocVi: "",
      hocHam: "",
      boMonId: "",
    },
  });

  // ====== mount: role + bo mon ======
  useEffect(() => {
    const info = localStorage.getItem("myInfo");
    if (info) {
      try {
        const parsed = JSON.parse(info);
        setRole(parsed?.role ?? null);
      } catch {}
    }
  }, []);

  const loadBoMonMap = async () => {
    setLoadingBoMon(true);
    try {
      const res = await getBoMonWithTBMPage({ page: 0, size: 1000, sort: "tenBoMon,ASC" });
      const list: BoMonWithTruongBoMonResponse[] = res.result?.content ?? [];
      const map: Record<number, string> = {};
      const opts: { id: number; tenBoMon: string }[] = [];
      for (const bm of list) {
        map[bm.id] = bm.tenBoMon;
        opts.push({ id: bm.id, tenBoMon: bm.tenBoMon });
      }
      setBoMonMap(map);
      setBoMonOptions(opts);
    } finally {
      setLoadingBoMon(false);
    }
  };

  // ====== load GV ======
  const loadData = async () => {
    setLoading(true);
    try {
      const res: PageType<GiangVien> = await getGiangVienPage({ page, size, sort });
      setData(res.content);
      setTotalPages(res.totalPages);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBoMonMap();
  }, []);

  useEffect(() => {
    loadData();
  }, [page, size, sort]);

  // ====== filter client-side theo mã/tên/email/tên bộ môn ======
  const filtered = useMemo(() => {
    const kw = q.trim().toLowerCase();
    if (!kw) return data;
    return data.filter((x) => {
      const bmName = x.boMonId != null ? (boMonMap[x.boMonId] ?? "") : "";
      return (
        x.maGV?.toLowerCase().includes(kw) ||
        x.hoTen?.toLowerCase().includes(kw) ||
        x.email?.toLowerCase().includes(kw) ||
        bmName.toLowerCase().includes(kw)
      );
    });
  }, [data, q, boMonMap]);

  // ====== handlers ======
  const onSubmitCreate = async (values: CreateFormValues) => {
    try {
      const payload = {
        maGV: values.maGV,
        hoTen: values.hoTen,
        soDienThoai: values.soDienThoai,
        email: values.email,
        matKhau: values.matKhau,
        hocVi: values.hocVi || undefined,
        hocHam: values.hocHam || undefined,
        boMonId: parseInt(values.boMonId, 10),
      };
      const res = await createGiangVien(payload);
      if (res.code === 1000) {
        toast.success("Thêm giảng viên thành công", { autoClose: 3000 });
        setOpenCreate(false);
        createForm.reset();
        // reload current page
        loadData();
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        const code = err.response?.data?.code;
        if (code === 1024) {
          toast.error("Email đã được sử dụng", { autoClose: 3000 });
        } else if (code === 1025) {
          toast.error("Mã giảng viên đã tồn tại", { autoClose: 3000 });
        } else {
          toast.error(`Thêm giảng viên thất bại: ${err.message}`, { autoClose: 3000 });
        }
      } else {
        toast.error("Thêm giảng viên thất bại", { autoClose: 3000 });
      }
    }
  };

  const onImport = async () => {
    if (!file) {
      toast.error("Vui lòng chọn file Excel trước khi import", { autoClose: 3000 });
      return;
    }
    try {
      const res = await importGiangVien(file);
      if (res.code === 1000) {
        toast.success("Import giảng viên thành công", { autoClose: 3000 });
        setOpenImport(false);
        setFile(null);
        loadData();
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(`Import thất bại: ${err.message}`, { autoClose: 3000 });
      } else {
        toast.error("Import thất bại", { autoClose: 3000 });
      }
    }
  };

  const onSetTroLyKhoa = async (gv: GiangVien) => {
    if (!gv.id) {
      toast.error("Không xác định được giảng viên", { autoClose: 3000 });
      return;
    }
    try {
      const res = await createTroLyKhoa({ giangVienId: gv.id });
      if (res.code === 1000) {
        toast.success("Đặt Trợ lý khoa thành công", { autoClose: 3000 });
      } else {
        toast.success(res.message ?? "Thực hiện thành công", { autoClose: 3000 });
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(`Không thể đặt Trợ lý khoa: ${err.response?.data?.message || err.message}`, {
          autoClose: 3000,
        });
      } else {
        toast.error("Không thể đặt Trợ lý khoa", { autoClose: 3000 });
      }
    }
  };

  return (
    <div>
      <h1 className="text-3xl text-center mt-10 font-bold mb-4">Danh sách giảng viên</h1>

      {/* Controls: Quyền + Sort + Search */}
      <div className="flex items-center justify-between gap-3 flex-wrap">
        {/* Left: Buttons theo quyền */}
        <div className="flex items-center gap-2">
          {(role === "ADMIN" || role === "TRO_LY_KHOA") && (
            <Dialog open={openCreate} onOpenChange={setOpenCreate}>
              <DialogTrigger asChild>
                <Button className="bg-[#457B9D] text-white hover:bg-[#35607a]">
                  <UserPlus className="w-4 h-4 mr-2" />
                  Thêm giảng viên
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[500px] bg-white border-none rounded-lg shadow-lg">
                <DialogHeader>
                  <DialogTitle className="text-center">Thêm giảng viên</DialogTitle>
                  <DialogDescription className="text-center">
                    Nhập thông tin giảng viên theo biểu mẫu bên dưới.
                  </DialogDescription>
                </DialogHeader>

                <form
                  onSubmit={createForm.handleSubmit(onSubmitCreate)}
                  className="grid grid-cols-1 md:grid-cols-2 gap-3"
                >
                  <div className="col-span-1">
                    <label className="text-sm font-medium">Mã GV</label>
                    <Input
                      {...createForm.register("maGV")}
                      placeholder="0123456789"
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                    {createForm.formState.errors.maGV && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.maGV.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Họ tên</label>
                    <Input
                      {...createForm.register("hoTen")}
                      placeholder="Nguyễn Văn A"
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                    {createForm.formState.errors.hoTen && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.hoTen.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Số điện thoại</label>
                    <Input
                      {...createForm.register("soDienThoai")}
                      placeholder="0912345678"
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                    {createForm.formState.errors.soDienThoai && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.soDienThoai.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Email</label>
                    <Input
                      type="email"
                      {...createForm.register("email")}
                      placeholder="example@email.com"
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                    {createForm.formState.errors.email && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.email.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Mật khẩu</label>
                    <Input
                      type="password"
                      {...createForm.register("matKhau")}
                      placeholder="********"
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                    {createForm.formState.errors.matKhau && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.matKhau.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Bộ môn</label>
                    <select
                      {...createForm.register("boMonId")}
                      className="mt-1 w-full border border-gray-300 rounded px-2 py-2"
                      disabled={loadingBoMon}
                    >
                      <option value="">-- Chọn bộ môn --</option>
                      {boMonOptions.map((bm) => (
                        <option key={bm.id} value={bm.id}>
                          {bm.tenBoMon}
                        </option>
                      ))}
                    </select>
                    {createForm.formState.errors.boMonId && (
                      <p className="text-red-500 text-sm mt-1">
                        {createForm.formState.errors.boMonId.message}
                      </p>
                    )}
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Học vị</label>
                    <Input
                      {...createForm.register("hocVi")}
                      placeholder="Thạc sĩ / Tiến sĩ..."
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                  </div>

                  <div className="col-span-1">
                    <label className="text-sm font-medium">Học hàm</label>
                    <Input
                      {...createForm.register("hocHam")}
                      placeholder="PGS / GS ..."
                      className="mt-1 border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                    />
                  </div>

                  <DialogFooter className="col-span-1 md:col-span-2 mt-2 flex gap-2">
                    <DialogClose asChild>
                      <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                        Trở về
                      </Button>
                    </DialogClose>
                    <Button type="submit" className="bg-[#457B9D] text-white hover:bg-[#35607a]">
                      Thêm giảng viên
                    </Button>
                  </DialogFooter>
                </form>
              </DialogContent>
            </Dialog>
          )}

          {role === "TRO_LY_KHOA" && (
            <Dialog open={openImport} onOpenChange={setOpenImport}>
              <DialogTrigger asChild>
                <Button className="bg-[#457B9D] text-white hover:bg-[#35607a]">
                  <Upload className="w-4 h-4 mr-2" />
                  Import giảng viên
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[480px] bg-white border-none rounded-lg shadow-lg">
                <DialogHeader>
                  <DialogTitle className="text-center">Import giảng viên</DialogTitle>
                  <DialogDescription className="text-center">
                    Tải file Excel mẫu, điền thông tin rồi upload.
                  </DialogDescription>
                </DialogHeader>

                <div className="mt-2 space-y-3">
                  <a className="text-blue-600 underline" download href="/assets/giangvien.xlsx">
                    Tải file mẫu
                  </a>
                  <Input
                    type="file"
                    accept=".xlsx,.xlsm"
                    onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                    className="border border-gray-300 focus-visible:ring-0 focus-visible:ring-offset-0"
                  />
                </div>

                <DialogFooter className="mt-4 flex gap-2">
                  <DialogClose asChild>
                    <Button className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]">
                      Trở về
                    </Button>
                  </DialogClose>
                  <Button onClick={onImport} className="bg-[#457B9D] text-white hover:bg-[#35607a]">
                    Thực hiện import
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        {/* Right: Sort + Search */}
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <span className="text-sm text-gray-600">Sắp xếp:</span>
            <select
              className="border border-gray-300 rounded px-2 py-1"
              value={sort}
              onChange={(e) => { setSort(e.target.value as typeof sort); setPage(0); }}
            >
              <option value="maGV,asc">Mã GV ↑</option>
              <option value="maGV,desc">Mã GV ↓</option>
              <option value="hoTen,asc">Họ tên ↑</option>
              <option value="hoTen,desc">Họ tên ↓</option>
            </select>
          </div>

          <form
            className="flex items-center gap-1"
            onSubmit={(e) => { e.preventDefault(); /* filter realtime */ }}
          >
            <Input
              type="text"
              placeholder="Tìm (mã, tên, email, bộ môn)..."
              className="w-[320px] border border-gray-300 h-10"
              value={q}
              onChange={(e) => setQ(e.target.value)}
            />
            <Button type="submit" className="h-10 border border-gray-300" variant="outline">
              <Search />
            </Button>
          </form>
        </div>
      </div>

      {/* Table */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">STT</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Mã GV</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Họ tên</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Bộ môn</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">SĐT</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Học vị</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Học hàm</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">Email</TableHead>
            {role === "ADMIN" && (
              <TableHead className="text-center font-semibold border border-gray-300">Hành động</TableHead>
            )}
          </TableRow>
        </TableHeader>

        <TableBody>
          {!loading && filtered.length === 0 && (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={role === "ADMIN" ? 9 : 8}>
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}

          {loading && (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={role === "ADMIN" ? 9 : 8}>
                Đang tải dữ liệu...
              </TableCell>
            </TableRow>
          )}

          {!loading && filtered.map((gv, i) => (
            <TableRow key={gv.id ?? `${gv.maGV}-${i}`} className="hover:bg-gray-50 transition-colors">
              <TableCell className="text-center border border-gray-300">{page * size + i + 1}</TableCell>
              <TableCell className="text-center border border-gray-300 font-medium">{gv.maGV}</TableCell>
              <TableCell className="text-center border border-gray-300">{gv.hoTen}</TableCell>
              <TableCell className="text-center border border-gray-300">
                {gv.boMonId != null
                  ? (boMonMap[gv.boMonId] ?? (loadingBoMon ? "Đang tải..." : "-"))
                  : "-"}
              </TableCell>
              <TableCell className="text-center border border-gray-300">{gv.soDienThoai ?? "-"}</TableCell>
              <TableCell className="text-center border border-gray-300">{gv.hocVi ?? "-"}</TableCell>
              <TableCell className="text-center border border-gray-300">{gv.hocHam ?? "-"}</TableCell>
              <TableCell className="text-center border border-gray-300">{gv.email ?? "-"}</TableCell>

              {role === "ADMIN" && (
                <TableCell className="text-center border border-gray-300">
                  <Button
                    size="sm"
                    variant="outline"
                    className="border border-gray-300"
                    onClick={() => onSetTroLyKhoa(gv)}
                    title="Đặt Trợ lý khoa"
                  >
                    <ShieldCheck className="w-4 h-4 mr-1" />
                    Đặt Trợ lý khoa
                  </Button>
                </TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Pagination */}
      <div className="flex justify-end mx-auto mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e)=>{ e.preventDefault(); if(page>0) setPage(page-1); }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
              page === 0 ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={(e)=>{ e.preventDefault(); setPage(i); }}
              className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 ${
                page === i ? "bg-[#2F80ED] text-white font-semibold" : "bg-gray-100 hover:bg-gray-200"
              }`}
            >
              {i + 1}
            </button>
          ))}

          <button
            onClick={(e)=>{ e.preventDefault(); if(page + 1 < totalPages) setPage(page + 1); }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
              page + 1 >= totalPages ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          <select
            className="border border-gray-300 rounded px-2 py-1 ml-2"
            value={size}
            onChange={(e)=>{ setSize(Number(e.target.value)); setPage(0); }}
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={20}>20</option>
          </select>
        </div>
      </div>
    </div>
  );
}

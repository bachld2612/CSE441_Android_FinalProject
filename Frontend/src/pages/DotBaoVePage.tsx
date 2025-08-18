// src/pages/DotBaoVePage.tsx
import { useEffect, useMemo, useState } from "react";
import { Plus, Search, ChevronLeft, ChevronRight, Calendar } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Pagination, PaginationContent, PaginationItem } from "@/components/ui/pagination";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useAuthStore } from "@/stores/authStore";
import { createDotBaoVe, updateDotBaoVe, getDotBaoVePage, type DotBaoVeRequest, type DotBaoVeResponse } from "@/services/dot-bao-ve.service";

function ColoredPencil({ className = "w-5 h-5" }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" className={className} aria-hidden>
      <rect x="5" y="12" width="12" height="4" rx="1" fill="#FFC107" transform="rotate(-45 5 12)" />
      <rect x="6" y="13" width="10" height="2" rx="1" fill="#FF9800" transform="rotate(-45 6 13)" />
      <polygon points="18,6 20.5,3.5 22,5 19.5,7.5" fill="#212121" />
      <rect x="3" y="14" width="3" height="4" rx="0.8" fill="#FF5C8A" transform="rotate(-45 3 14)" />
      <path d="M18 6 L20.5 3.5" stroke="white" strokeWidth="0.6" />
    </svg>
  );
}

export default function DotBaoVePage() {
  const [rows, setRows] = useState<DotBaoVeResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<DotBaoVeResponse | null>(null);

  const [ten, setTen] = useState("");
  const [hocKi, setHocKi] = useState<number | "">("");
  const [batDau, setBatDau] = useState("");
  const [ketThuc, setKetThuc] = useState("");
  const [namBD, setNamBD] = useState<number | "">("");
  const [namKT, setNamKT] = useState<number | "">("");

  const [query, setQuery] = useState("");
  const token = useAuthStore((s) => s.token);

  // Quyền: chỉ TRO_LY_KHOA được thêm/sửa
  const [role, setRole] = useState<string | null>(null);
  const canManage = role === "TRO_LY_KHOA";

  const fetchPage = async (p = page) => {
    setLoading(true);
    try {
      const res = await getDotBaoVePage({ page: p, size, sort: "updatedAt,DESC" });
      setRows(res.result?.content ?? []);
      setTotalPages(res.result?.totalPages ?? 0);
    } catch {
      toast.error("Không thể tải danh sách đợt đồ án");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const info = localStorage.getItem("myInfo");
    if (info) {
      try {
        const parsed = JSON.parse(info);
        setRole(parsed?.role ?? null);
      } catch (e) {
        console.error("Failed to parse myInfo:", e);
      }
    }
  }, []);

  useEffect(() => {
    if (!token) return;
    fetchPage(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const filtered = useMemo(() => {
    if (!query.trim()) return rows;
    const q = query.toLowerCase().trim();
    return rows.filter((d) =>
      (d.tenDotBaoVe ?? "").toLowerCase().includes(q) ||
      String(d.hocKi ?? "").includes(q) ||
      `${d.namBatDau ?? ""}-${d.namKetThuc ?? ""}`.includes(q)
    );
  }, [rows, query]);

  const openAdd = () => {
    setEditing(null);
    setTen("");
    setHocKi("");
    setBatDau("");
    setKetThuc("");
    setNamBD("");
    setNamKT("");
    setOpen(true);
  };

  const openEdit = (d: DotBaoVeResponse) => {
    setEditing(d);
    setTen(d.tenDotBaoVe ?? "");
    setHocKi(d.hocKi ?? "");
    setBatDau((d.thoiGianBatDau || "").slice(0, 10));
    setKetThuc((d.thoiGianKetThuc || "").slice(0, 10));
    setNamBD(d.namBatDau ?? "");
    setNamKT(d.namKetThuc ?? "");
    setOpen(true);
  };

  const handleSave = async () => {
    if (!ten.trim()) return toast.error("Tên đợt bảo vệ không được để trống");
    if (!hocKi || Number(hocKi) <= 0) return toast.error("Học kì không hợp lệ");
    if (!batDau || !ketThuc) return toast.error("Vui lòng chọn thời gian bắt đầu/kết thúc");
    if (!namBD || !namKT) return toast.error("Vui lòng nhập năm bắt đầu/kết thúc");

    const data: DotBaoVeRequest = {
      tenDotBaoVe: ten.trim(),
      hocKi: Number(hocKi),
      thoiGianBatDau: batDau,
      thoiGianKetThuc: ketThuc,
      namBatDau: Number(namBD),
      namKetThuc: Number(namKT),
    };

    setSubmitting(true);
    try {
      const res = editing ? await updateDotBaoVe(editing.id!, data) : await createDotBaoVe(data);
      if (res.result) {
        toast.success(editing ? "Cập nhật thành công" : "Thêm mới thành công");
        setOpen(false);
        setEditing(null);
        fetchPage(page);
      } else {
        toast.error(res.message || "Thao tác thất bại");
      }
    } catch (e: any) {
      toast.error(e?.response?.data?.message || "Không thể lưu dữ liệu");
    } finally {
      setSubmitting(false);
    }
  };

  const setAndFetch = (p: number) => {
    if (p < 0 || p >= totalPages || p === page) return;
    setPage(p);
    fetchPage(p);
  };

  return (
    <div className="space-y-6">
      <div className="text-sm text-gray-500">
        Trang chủ / Quản lý tổ chức / <span className="text-gray-700 font-medium">Quản lý đợt đồ án</span>
      </div>

      <h1 className="text-3xl font-bold text-center">Quản lý đợt đồ án</h1>

      <div className="flex items-center justify-between">
        {canManage && (
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button
                onClick={openAdd}
                className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                title="Thêm đợt đồ án"
              >
                <Plus className="w-4 h-4 mr-2" />
                Thêm đợt đồ án
              </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-xl bg-white" aria-describedby="dotbv-desc">
              <DialogHeader>
                <DialogTitle>{editing ? "Sửa đợt đồ án" : "Thêm đợt đồ án"}</DialogTitle>
                <DialogDescription id="dotbv-desc" className="sr-only">
                  Biểu mẫu {editing ? "cập nhật" : "tạo mới"} đợt đồ án.
                </DialogDescription>
              </DialogHeader>

              <div className="grid gap-4">
                <div className="grid gap-2 md:grid-cols-2">
                  <div className="grid gap-2">
                    <Label htmlFor="tenDotInput">Tên đợt bảo vệ</Label>
                    <Input
                      id="tenDotInput"
                      value={ten}
                      onChange={(e) => setTen(e.target.value)}
                      autoComplete="off"
                      placeholder="VD: Đợt bảo vệ HK1 2025"
                      className="border border-gray-300"
                    />
                  </div>

                  <div className="grid gap-2">
                    <Label htmlFor="hocKiInput">Học kì</Label>
                    <Input
                      id="hocKiInput"
                      type="number"
                      min={1}
                      max={3}
                      value={hocKi}
                      onChange={(e) => setHocKi(e.target.value ? Number(e.target.value) : "")}
                      autoComplete="off"
                      placeholder="1 | 2 | 3"
                      className="border border-gray-300"
                    />
                  </div>
                </div>

                <div className="grid gap-2 md:grid-cols-2">
                  {/* Bắt đầu */}
                  <div className="grid gap-2">
                    <Label htmlFor="batDauInput">Thời gian bắt đầu</Label>
                    <div className="relative">
                      <Input
                        id="batDauInput"
                        type="date"
                        value={batDau}
                        onChange={(e) => setBatDau(e.target.value)}
                        className="pr-10 border border-gray-300
                          [color-scheme:light]
                          [&::-webkit-inner-spin-button]:hidden
                          [&::-webkit-clear-button]:hidden
                          [&::-webkit-calendar-picker-indicator]:opacity-0
                          [&::-webkit-calendar-picker-indicator]:absolute
                          [&::-webkit-calendar-picker-indicator]:right-3
                          [&::-webkit-calendar-picker-indicator]:w-6
                          [&::-webkit-calendar-picker-indicator]:h-6"
                      />
                      <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500 pointer-events-none" />
                    </div>
                  </div>

                  {/* Kết thúc */}
                  <div className="grid gap-2">
                    <Label htmlFor="ketThucInput">Thời gian kết thúc</Label>
                    <div className="relative">
                      <Input
                        id="ketThucInput"
                        type="date"
                        value={ketThuc}
                        onChange={(e) => setKetThuc(e.target.value)}
                        className="pr-10 border border-gray-300
                          [color-scheme:light]
                          [&::-webkit-inner-spin-button]:hidden
                          [&::-webkit-clear-button]:hidden
                          [&::-webkit-calendar-picker-indicator]:opacity-0
                          [&::-webkit-calendar-picker-indicator]:absolute
                          [&::-webkit-calendar-picker-indicator]:right-3
                          [&::-webkit-calendar-picker-indicator]:w-6
                          [&::-webkit-calendar-picker-indicator]:h-6"
                      />
                      <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500 pointer-events-none" />
                    </div>
                  </div>
                </div>

                <div className="grid gap-2 md:grid-cols-2">
                  <div className="grid gap-2">
                    <Label htmlFor="namBDInput">Năm bắt đầu</Label>
                    <Input
                      id="namBDInput"
                      type="number"
                      value={namBD}
                      onChange={(e) => setNamBD(e.target.value ? Number(e.target.value) : "")}
                      autoComplete="off"
                      placeholder="2025"
                      className="border border-gray-300"
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="namKTInput">Năm kết thúc</Label>
                    <Input
                      id="namKTInput"
                      type="number"
                      value={namKT}
                      onChange={(e) => setNamKT(e.target.value ? Number(e.target.value) : "")}
                      autoComplete="off"
                      placeholder="2026"
                      className="border border-gray-300"
                    />
                  </div>
                </div>
              </div>

              <DialogFooter className="flex gap-2">
                <Button
                  type="button"
                  variant="secondary"
                  className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]"
                  onClick={() => setOpen(false)}
                  disabled={submitting}
                  title="Đóng"
                >
                  Trở về
                </Button>
                <Button
                  type="button"
                  className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                  onClick={handleSave}
                  disabled={submitting}
                  title={editing ? "Cập nhật đợt đồ án" : "Tạo mới đợt đồ án"}
                >
                  {editing ? "Cập nhật" : "Tạo mới"}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        )}

        {/* Search */}
        <div className="flex items-center gap-2">
          <Input
            className="w-64 border border-gray-300"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Tên/Học kì/Năm"
            name="searchDotBaoVe"
            autoComplete="off"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm" className="border border-gray-300">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Bảng + pagination giống trang Giảng viên (border gray-300, header bg-gray-100, căn giữa, không ID/STT) */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300 w-[40%]">Tên đợt</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[10%]">Học kì</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[25%]">Thời gian</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[15%]">Năm học</TableHead>
            {canManage && (
              <TableHead className="text-center font-semibold border border-gray-300 w-[10%]">Hành động</TableHead>
            )}
          </TableRow>
        </TableHeader>
        <TableBody>
          {loading ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 5 : 4}>
                Đang tải…
              </TableCell>
            </TableRow>
          ) : filtered.length === 0 ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 5 : 4}>
                Không có dữ liệu
              </TableCell>
            </TableRow>
          ) : (
            filtered.map((d) => (
              <TableRow key={`${d.tenDotBaoVe}-${d.thoiGianBatDau}-${d.namBatDau}`} className="hover:bg-gray-50 transition-colors">
                <TableCell className="text-center border border-gray-300">{d.tenDotBaoVe}</TableCell>
                <TableCell className="text-center border border-gray-300">{d.hocKi}</TableCell>
                <TableCell className="text-center border border-gray-300">
                  {d.thoiGianBatDau?.slice(0, 10)} → {d.thoiGianKetThuc?.slice(0, 10)}
                </TableCell>
                <TableCell className="text-center border border-gray-300">
                  {d.namBatDau} - {d.namKetThuc}
                </TableCell>
                {canManage && (
                  <TableCell className="text-center border border-gray-300">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEdit(d)}
                      title="Sửa"
                      type="button"
                      className="border border-gray-300"
                    >
                      <ColoredPencil />
                    </Button>
                  </TableCell>
                )}
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>

      {/* Pagination canh phải, nút tròn */}
      <div className="w-full px-4 py-4 border-t flex justify-end">
        <Pagination>
          <PaginationContent className="flex items-center gap-2">
            <PaginationItem>
              <button
                onClick={(e) => { e.preventDefault(); if (page > 0) setAndFetch(page - 1); }}
                className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
                  page === 0 ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
                }`}
                aria-label="Trang trước"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
            </PaginationItem>

            {Array.from({ length: totalPages }, (_, i) => (
              <PaginationItem key={i}>
                <button
                  onClick={(e) => { e.preventDefault(); setAndFetch(i); }}
                  className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 ${
                    page === i ? "bg-[#2F80ED] text-white font-semibold" : "bg-gray-100 hover:bg-gray-200"
                  }`}
                  aria-current={page === i ? "page" : undefined}
                  aria-label={`Trang ${i + 1}`}
                >
                  {i + 1}
                </button>
              </PaginationItem>
            ))}

            <PaginationItem>
              <button
                onClick={(e) => { e.preventDefault(); if (page + 1 < totalPages) setAndFetch(page + 1); }}
                className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
                  page + 1 >= totalPages ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
                }`}
                aria-label="Trang sau"
              >
                <ChevronRight className="w-4 h-4 " />
              </button>
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    </div>
  );
}

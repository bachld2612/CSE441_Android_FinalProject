// src/pages/NganhPage.tsx
import { useEffect, useMemo, useState } from "react";
import { ChevronLeft, ChevronRight, Pencil, Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import { getAllKhoa, type KhoaResponse } from "@/services/khoa.service";
import {
  getAllNganh, createNganh, updateNganh,
  type NganhResponse, type NganhRequest,
} from "@/services/nganh.service";

import { useAuthStore } from "@/stores/authStore";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export default function NganhPage() {
  const [items, setItems] = useState<NganhResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);

  // Dialog state
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<NganhResponse | null>(null);

  // Form
  const [tenNganh, setTenNganh] = useState("");
  const [khoaId, setKhoaId] = useState<number | null>(null);

  // Search
  const [query, setQuery] = useState("");

  // Role
  const token = useAuthStore((s) => s.token);
  const [role, setRole] = useState<string | null>(null);
  const canManage = role === "ADMIN" || role === "TRO_LY_KHOA";

  // Pagination (client-side)
  const [page, setPage] = useState(0); // 0-based
  const [size, setSize] = useState(10);

  const fetchNganh = async () => {
    setLoading(true);
    try {
      const res = await getAllNganh();
      setItems(res.result ?? []);
    } catch {
      toast.error("Không thể tải danh sách ngành");
    } finally {
      setLoading(false);
    }
  };

  const fetchKhoa = async () => {
    try {
      const res = await getAllKhoa();
      setKhoas(res.result ?? []);
    } catch {
      /* im lặng */
    }
  };

  useEffect(() => {
    const info = localStorage.getItem("myInfo");
    if (info) {
      try {
        const parsed = JSON.parse(info);
        setRole(parsed?.role ?? null);
      } catch {}
    }
  }, []);

  useEffect(() => {
    if (!token) return;
    fetchKhoa();
    fetchNganh();
  }, [token]);

  const khoaMap = useMemo(
    () => new Map(khoas.map((k) => [k.id, k.tenKhoa ?? ""])),
    [khoas]
  );

  // Lọc theo tên/khoa (không dùng ID/Mã)
  const filtered = useMemo(() => {
    if (!query.trim()) return items;
    const q = query.toLowerCase().trim();
    return items.filter((n) => {
      const name = (n.tenNganh ?? "").toLowerCase();
      const kName = (khoaMap.get(n.khoaId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || kName.includes(q);
    });
  }, [items, query, khoaMap]);

  // Tính trang dựa trên filtered
  const totalPages = Math.max(1, Math.ceil(filtered.length / size));
  const pageClamped = Math.min(page, totalPages - 1);
  const paginated = useMemo(() => {
    const start = pageClamped * size;
    return filtered.slice(start, start + size);
  }, [filtered, pageClamped, size]);

  // Reset về trang 0 khi đổi filter/size
  useEffect(() => { setPage(0); }, [query, size]);

  const openAddDialog = () => {
    setEditing(null);
    setTenNganh("");
    setKhoaId(null);
    setOpen(true);
  };

  const openEditDialog = (n: NganhResponse) => {
    setEditing(n);
    setTenNganh(n.tenNganh ?? "");
    setKhoaId(n.khoaId ?? null);
    setOpen(true);
  };

  const handleSave = async () => {
    const name = tenNganh.trim();
    if (!name) return toast.error("Tên ngành không được để trống");
    if (!khoaId) return toast.error("Vui lòng chọn khoa");

    setSubmitting(true);
    try {
      const payload: NganhRequest = { tenNganh: name, khoaId };
      const res = editing ? await updateNganh(editing.id, payload) : await createNganh(payload);
      if (res.result) {
        toast.success(editing ? "Cập nhật ngành thành công" : "Thêm ngành thành công");
        setOpen(false);
        setEditing(null);
        setTenNganh("");
        setKhoaId(null);
        fetchNganh();
      } else {
        toast.error(res.message || "Thao tác thất bại");
      }
    } catch {
      toast.error("Không thể lưu thông tin");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500">
        Trang chủ / Quản lý tổ chức / <span className="text-gray-700 font-medium">Quản lý ngành</span>
      </div>

      {/* Title */}
      <h1 className="text-3xl font-bold text-center">Quản lý ngành</h1>

      {/* Actions (chỉ ADMIN & TRỢ LÝ KHOA) + Search */}
      <div className="flex items-center justify-between">
        {canManage && (
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button
                onClick={openAddDialog}
                className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                aria-label="Thêm ngành"
                title="Thêm ngành"
                type="button"
              >
                <Plus className="w-4 h-4 mr-2" />
                Thêm ngành
              </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-md bg-white" aria-describedby="nganh-dialog-desc">
              <DialogHeader>
                <DialogTitle>{editing ? "Sửa ngành" : "Thêm ngành"}</DialogTitle>
                <DialogDescription id="nganh-dialog-desc" className="sr-only">
                  Biểu mẫu {editing ? "cập nhật" : "tạo mới"} ngành.
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-4">
                <div className="grid gap-2">
                  <Label htmlFor="tenNganhInput">Tên ngành</Label>
                  <Input
                    id="tenNganhInput"
                    name="tenNganh"
                    autoComplete="off"
                    value={tenNganh}
                    onChange={(e) => setTenNganh(e.target.value)}
                    placeholder="Ví dụ: Công nghệ thông tin"
                    onKeyDown={(e) => { if (e.key === "Enter") handleSave(); }}
                    className="border border-gray-300"
                  />
                </div>

                <div className="grid gap-2">
                  <Label htmlFor="khoaSelect">Khoa</Label>
                  <Select
                    value={khoaId ? String(khoaId) : ""}
                    onValueChange={(v) => setKhoaId(Number(v))}
                  >
                    <SelectTrigger id="khoaSelect" aria-label="Chọn khoa" className="h-10 rounded-xl border border-gray-300 bg-white">
                      <SelectValue placeholder="Chọn khoa" />
                    </SelectTrigger>

                    <SelectContent
                      position="popper"
                      className="bg-white text-gray-900 border border-gray-200 rounded-xl shadow-lg overflow-hidden"
                    >
                      {khoas.map((k) => (
                        <SelectItem
                          key={k.id}
                          value={String(k.id)}
                          className="cursor-pointer data-[highlighted]:bg-[#EFF6FF] data-[highlighted]:text-[#006EFF]"
                        >
                          {k.tenKhoa}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
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
                  title={editing ? "Cập nhật ngành" : "Tạo mới ngành"}
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
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Tên ngành/Khoa"
            name="searchNganh"
            autoComplete="off"
            className="w-64 border border-gray-300"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm" className="border border-gray-300">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Table: giống Giảng viên (STT + viền gray-300) */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300 w-[10%]">STT</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[45%]">Tên ngành</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[25%]">Khoa</TableHead>
            {canManage && (
              <TableHead className="text-center font-semibold border border-gray-300 w-[20%]">Hành động</TableHead>
            )}
          </TableRow>
        </TableHeader>

        <TableBody>
          {loading ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 4 : 3}>
                Đang tải...
              </TableCell>
            </TableRow>
          ) : paginated.length === 0 ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 4 : 3}>
                Không có dữ liệu
              </TableCell>
            </TableRow>
          ) : (
            paginated.map((n, i) => (
              <TableRow key={n.id} className="hover:bg-gray-50 transition-colors">
                <TableCell className="text-center border border-gray-300">
                  {pageClamped * size + i + 1}
                </TableCell>
                <TableCell className="text-center border border-gray-300">{n.tenNganh}</TableCell>
                <TableCell className="text-center border border-gray-300">
                  {khoaMap.get(n.khoaId ?? -1) ?? ""}
                </TableCell>
                {canManage && (
                  <TableCell className="text-center border border-gray-300">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(n)}
                      aria-label={`Sửa ngành ${n.tenNganh ?? ""}`}
                      title="Sửa"
                      type="button"
                      className="border border-gray-300"
                    >
                      <Pencil className="w-4 h-4" />
                    </Button>
                  </TableCell>
                )}
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>

      {/* Pagination (client-side) — giống style Giảng viên */}
      <div className="flex justify-end mx-auto mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e)=>{ e.preventDefault(); if(pageClamped>0) setPage(pageClamped-1); }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
              pageClamped === 0 ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
            aria-label="Trang trước"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={(e)=>{ e.preventDefault(); setPage(i); }}
              className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 ${
                pageClamped === i ? "bg-[#2F80ED] text-white font-semibold" : "bg-gray-100 hover:bg-gray-200"
              }`}
              aria-current={pageClamped === i ? "page" : undefined}
              aria-label={`Trang ${i + 1}`}
            >
              {i + 1}
            </button>
          ))}

          <button
            onClick={(e)=>{ e.preventDefault(); if(pageClamped + 1 < totalPages) setPage(pageClamped + 1); }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
              pageClamped + 1 >= totalPages ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
            aria-label="Trang sau"
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          <select
            className="border border-gray-300 rounded px-2 py-1 ml-2"
            value={size}
            onChange={(e)=>{ setSize(Number(e.target.value)); }}
            aria-label="Kích thước trang"
            title="Kích thước trang"
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

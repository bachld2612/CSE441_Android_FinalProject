// src/pages/LopPage.tsx
import { useEffect, useMemo, useState } from "react";
import { ChevronLeft, ChevronRight, Pencil, Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import { useAuthStore } from "@/stores/authStore";
import { getAllNganh, type NganhResponse } from "@/services/nganh.service";
import {
  getAllLop, createLop, updateLop,
  type LopResponse, type LopRequest,
} from "@/services/lop.service";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";

export default function LopPage() {
  const [items, setItems] = useState<LopResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const [nganhs, setNganhs] = useState<NganhResponse[]>([]);

  // Dialog state
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<LopResponse | null>(null);

  // Form
  const [tenLop, setTenLop] = useState("");
  const [nganhId, setNganhId] = useState<number | null>(null);

  // Search
  const [query, setQuery] = useState("");

  // Quyền
  const token = useAuthStore((s) => s.token);
  const [role, setRole] = useState<string | null>(null);
  const canManage = role === "TRO_LY_KHOA"; // chỉ Trợ lý khoa được thêm/sửa

  // Phân trang (client-side)
  const [page, setPage] = useState(0); // 0-based
  const [size, setSize] = useState(10);

  const fetchLop = async () => {
    setLoading(true);
    try {
      const res = await getAllLop();
      setItems(res.result ?? []);
    } catch {
      toast.error("Không thể tải danh sách lớp");
    } finally {
      setLoading(false);
    }
  };

  const fetchNganh = async () => {
    try {
      const res = await getAllNganh();
      setNganhs(res.result ?? []);
    } catch { /* im lặng */ }
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
    fetchNganh();
    fetchLop();
  }, [token]);

  const nganhMap = useMemo(
    () => new Map(nganhs.map((n) => [n.id, n.tenNganh ?? ""])),
    [nganhs]
  );

  // Lọc giống trang Giảng viên: theo tên lớp và tên ngành (không dùng ID/mã)
  const filtered = useMemo(() => {
    if (!query.trim()) return items;
    const q = query.toLowerCase().trim();
    return items.filter((l) => {
      const name = (l.tenLop ?? "").toLowerCase();
      const nName = (nganhMap.get(l.nganhId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || nName.includes(q);
    });
  }, [items, query, nganhMap]);

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
    setTenLop("");
    setNganhId(null);
    setOpen(true);
  };

  const openEditDialog = (l: LopResponse) => {
    setEditing(l);
    setTenLop(l.tenLop ?? "");
    setNganhId(l.nganhId ?? null);
    setOpen(true);
  };

  const handleSave = async () => {
    const name = tenLop.trim();
    if (!name) return toast.error("Tên lớp không được để trống");
    if (!nganhId) return toast.error("Vui lòng chọn ngành");

    setSubmitting(true);
    try {
      const payload: LopRequest = { tenLop: name, nganhId };
      const res = editing
        ? await updateLop(editing.id, payload)
        : await createLop(payload);

      if (res.result) {
        toast.success(editing ? "Cập nhật lớp thành công" : "Thêm lớp thành công");
        setOpen(false);
        setEditing(null);
        setTenLop("");
        setNganhId(null);
        fetchLop();
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
        Trang chủ / Quản lý tổ chức / <span className="text-gray-700 font-medium">Quản lý lớp</span>
      </div>

      {/* Title */}
      <h1 className="text-3xl font-bold text-center">Quản lý lớp</h1>

      {/* Actions + Search */}
      <div className="flex items-center justify-between">
        {canManage && (
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button
                onClick={openAddDialog}
                className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                aria-label="Thêm lớp"
                title="Thêm lớp"
                type="button"
              >
                <Plus className="w-4 h-4 mr-2" />
                Thêm lớp
              </Button>
            </DialogTrigger>

            {/* Dialog */}
            <DialogContent className="sm:max-w-md bg-white" aria-describedby="lop-dialog-desc">
              <DialogHeader>
                <DialogTitle>{editing ? "Sửa lớp" : "Thêm lớp"}</DialogTitle>
                <DialogDescription id="lop-dialog-desc" className="sr-only">
                  Biểu mẫu {editing ? "cập nhật" : "tạo mới"} lớp.
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-4">
                <div className="grid gap-2">
                  <Label htmlFor="tenLopInput">Tên lớp</Label>
                  <Input
                    id="tenLopInput"
                    name="tenLop"
                    autoComplete="off"
                    value={tenLop}
                    onChange={(e) => setTenLop(e.target.value)}
                    placeholder="Ví dụ: K66-CNTT1"
                    onKeyDown={(e) => { if (e.key === "Enter") handleSave(); }}
                    className="border border-gray-300"
                  />
                </div>

                <div className="grid gap-2">
                  <Label htmlFor="nganhSelect">Ngành</Label>
                  <Select
                    value={nganhId ? String(nganhId) : ""}
                    onValueChange={(v) => setNganhId(Number(v))}
                  >
                    <SelectTrigger
                      id="nganhSelect"
                      aria-label="Chọn ngành"
                      className="
                        h-10 rounded-xl
                        border border-gray-300
                        bg-white text-gray-900
                        hover:bg-gray-50
                        focus:outline-none focus:ring-2 focus:ring-[#457B9D]
                        data-[state=open]:ring-2 data-[state=open]:ring-[#457B9D]
                      "
                    >
                      <SelectValue placeholder="Chọn ngành" />
                    </SelectTrigger>
                    <SelectContent
                      position="popper"
                      className="bg-white text-gray-900 border border-gray-200 rounded-xl shadow-lg overflow-hidden"
                    >
                      {nganhs.map((n) => (
                        <SelectItem
                          key={n.id}
                          value={String(n.id)}
                          className="cursor-pointer data-[highlighted]:bg-[#EFF6FF] data-[highlighted]:text-[#006EFF]"
                        >
                          {n.tenNganh}
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
                  title={editing ? "Cập nhật lớp" : "Tạo mới lớp"}
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
            placeholder="Tên lớp/Ngành"
            name="searchLop"
            autoComplete="off"
            className="w-64 border border-gray-300"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm" className="border border-gray-300">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Table — giống Giảng viên */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300 w-[40%]">Tên lớp</TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[40%]">Ngành</TableHead>
            {canManage && (
              <TableHead className="text-center font-semibold border border-gray-300 w-[20%]">Hành động</TableHead>
            )}
          </TableRow>
        </TableHeader>

        <TableBody>
          {loading ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 3 : 2}>
                Đang tải...
              </TableCell>
            </TableRow>
          ) : paginated.length === 0 ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 3 : 2}>
                Không có dữ liệu
              </TableCell>
            </TableRow>
          ) : (
            paginated.map((l) => (
              <TableRow key={l.id} className="hover:bg-gray-50 transition-colors">
                <TableCell className="text-center border border-gray-300">{l.tenLop}</TableCell>
                <TableCell className="text-center border border-gray-300">
                  {nganhMap.get(l.nganhId ?? -1) ?? ""}
                </TableCell>
                {canManage && (
                  <TableCell className="text-center border border-gray-300">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(l)}
                      aria-label={`Sửa lớp ${l.tenLop ?? ""}`}
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

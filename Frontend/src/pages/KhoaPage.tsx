import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import {
  getAllKhoa, createKhoa, updateKhoa,
  type KhoaResponse, type KhoaRequest,
} from "@/services/khoa.service";
import { useAuthStore } from "@/stores/authStore";
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb";

export default function KhoaPage() {
  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);
  const [loading, setLoading] = useState(false);

  // Dialog
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<KhoaResponse | null>(null);

  // Form (chỉ có Tên khoa)
  const [tenKhoa, setTenKhoa] = useState("");

  // Search + Sort + Pagination (client-side)
  const [query, setQuery] = useState("");
  const [sort, setSort] = useState<"tenKhoa,asc" | "tenKhoa,desc" | "id,asc" | "id,desc">("tenKhoa,asc");
  const [page, setPage] = useState(0); // 0-based
  const [size, setSize] = useState(10);

  const token = useAuthStore((s) => s.token);

  // Role
  const [role, setRole] = useState<string | null>(null);
  useEffect(() => {
    const info = localStorage.getItem("myInfo");
    if (info) {
      try {
        const parsed = JSON.parse(info);
        setRole(parsed?.role ?? null);
      } catch (e){
        console.error("Failed to parse myInfo:", e);
      }
    }
  }, []);

  const fetchKhoas = async () => {
    setLoading(true);
    try {
      const res = await getAllKhoa(); // ApiResponse<KhoaResponse[]>
      setKhoas(res.result ?? []);
    } catch {
      toast.error("Không thể tải danh sách khoa");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!token) return; // chỉ gọi khi đã có token
    fetchKhoas();
  }, [token]);

  // Filter
  const filtered = useMemo(() => {
    if (!query.trim()) return khoas;
    const q = query.toLowerCase().trim();
    return khoas.filter(
      (k) => (k.tenKhoa ?? "").toLowerCase().includes(q) || String(k.id).includes(q)
    );
  }, [khoas, query]);

  // Sort
  const sorted = useMemo(() => {
    const [key, dir] = sort.split(",") as ["tenKhoa" | "id", "asc" | "desc"];
    const mul = dir === "asc" ? 1 : -1;
    return [...filtered].sort((a, b) => {
      const va = key === "id" ? (a.id ?? 0) : (a.tenKhoa ?? "");
      const vb = key === "id" ? (b.id ?? 0) : (b.tenKhoa ?? "");
      if (typeof va === "number" && typeof vb === "number") return (va - vb) * mul;
      return String(va).localeCompare(String(vb)) * mul;
    });
  }, [filtered, sort]);

  // Pagination (client)
  const totalPages = Math.max(1, Math.ceil(sorted.length / size));
  const pageData = useMemo(() => {
    const start = page * size;
    return sorted.slice(start, start + size);
  }, [sorted, page, size]);

  // reset page khi query/sort thay đổi
  useEffect(() => { setPage(0); }, [query, sort]);

  const openAddDialog = () => {
    setEditing(null);
    setTenKhoa("");
    setOpen(true);
  };

  const openEditDialog = (k: KhoaResponse) => {
    setEditing(k);
    setTenKhoa(k.tenKhoa ?? "");
    setOpen(true);
  };

  const handleSave = async () => {
    const value = tenKhoa.trim();
    if (!value) {
      toast.error("Tên khoa không được để trống");
      return;
    }
    setSubmitting(true);
    try {
      const payload: KhoaRequest = { tenKhoa: value };
      const res = editing
        ? await updateKhoa(editing.id, payload)
        : await createKhoa(payload);

      if (res.result) {
        toast.success(editing ? "Cập nhật khoa thành công" : "Thêm khoa thành công");
        setOpen(false);
        setEditing(null);
        setTenKhoa("");
        fetchKhoas();
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
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="#">
              Khoa
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      {/* Tiêu đề */}
      <h1 className="text-3xl font-bold text-center">Quản lý khoa</h1>

      {/* Thanh hành động giống trang Giảng viên: Buttons (trái) + Sort/Search (phải) */}
      <div className="flex items-center justify-between gap-3 flex-wrap">
        {/* Left: chỉ ADMIN được thêm */}
        <div className="flex items-center gap-2">
          {role === "ADMIN" && (
            <Dialog open={open} onOpenChange={setOpen}>
              <DialogTrigger asChild>
                <Button onClick={openAddDialog} className="bg-[#457B9D] text-white hover:bg-[#3b6b86]">
                  <Plus className="w-4 h-4 mr-2" /> Thêm khoa
                </Button>
              </DialogTrigger>

              <DialogContent
                className="sm:max-w-md bg-white"
                aria-describedby={editing ? "khoa-edit-desc" : "khoa-create-desc"}
              >
                <DialogHeader>
                  <DialogTitle>{editing ? "Sửa khoa" : "Thêm khoa"}</DialogTitle>
                  <p id={editing ? "khoa-edit-desc" : "khoa-create-desc"} className="sr-only">
                    Biểu mẫu {editing ? "cập nhật" : "tạo mới"} khoa.
                  </p>
                </DialogHeader>

                <div className="space-y-4">
                  <div className="grid gap-2">
                    <Label htmlFor="tenKhoaInput">Tên khoa</Label>
                    <Input
                      id="tenKhoaInput"
                      name="tenKhoa"
                      autoComplete="off"
                      value={tenKhoa}
                      onChange={(e) => setTenKhoa(e.target.value)}
                      placeholder="Ví dụ: Khoa Công nghệ Thông tin"
                      onKeyDown={(e) => e.key === "Enter" && handleSave()}
                      className="border border-gray-300"
                    />
                  </div>
                </div>

                <DialogFooter className="flex gap-2">
                  <Button
                    type="button"
                    variant="secondary"
                    className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]"
                    onClick={() => setOpen(false)}
                    disabled={submitting}
                  >
                    Trở về
                  </Button>
                  <Button
                    type="button"
                    className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                    onClick={handleSave}
                    disabled={submitting}
                  >
                    {editing ? "Cập nhật" : "Tạo mới"}
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
              onChange={(e) => setSort(e.target.value as typeof sort)}
            >
              <option value="tenKhoa,asc">Tên khoa ↑</option>
              <option value="tenKhoa,desc">Tên khoa ↓</option>
            </select>
          </div>

          <form className="flex items-center gap-1" onSubmit={(e) => e.preventDefault()}>
            <Input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Tên khoa"
              name="searchKhoa"
              autoComplete="off"
              className="w-64 border border-gray-300 h-10"
            />
            <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm" className="border border-gray-300">
              <Search className="w-4 h-4" />
            </Button>
          </form>
        </div>
      </div>

      {/* Bảng danh sách — style giống trang Giảng viên */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-300 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-100">
              <TableHead className="text-center font-semibold border border-gray-300 w-[100px]">STT</TableHead>
              <TableHead className="text-center font-semibold border border-gray-300">Tên khoa</TableHead>
              {role === "ADMIN" && (
                <TableHead className="text-center font-semibold border border-gray-300 w-[140px]">Hành động</TableHead>
              )}
            </TableRow>
          </TableHeader>

          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell className="text-center border border-gray-300" colSpan={role === "ADMIN" ? 4 : 3}>
                  Đang tải...
                </TableCell>
              </TableRow>
            ) : pageData.length === 0 ? (
              <TableRow>
                <TableCell className="text-center border border-gray-300" colSpan={role === "ADMIN" ? 4 : 3}>
                  Không có dữ liệu
                </TableCell>
              </TableRow>
            ) : (
              pageData.map((khoa, idx) => (
                <TableRow key={khoa.id} className="hover:bg-gray-50 transition-colors">
                  <TableCell className="text-center border border-gray-300">
                    {page * size + idx + 1}
                  </TableCell>
                  <TableCell className="text-center border border-gray-300">
                    {khoa.tenKhoa}
                  </TableCell>

                  {role === "ADMIN" && (
                    <TableCell className="text-center border border-gray-300">
                      <Button
                        size="icon"
                        variant="outline"
                        className="border border-gray-300"
                        onClick={() => openEditDialog(khoa)}
                        aria-label={`Sửa khoa ${khoa.tenKhoa}`}
                        title="Sửa"
                        type="button"
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
      </div>

      {/* Pagination giống trang Giảng viên */}
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

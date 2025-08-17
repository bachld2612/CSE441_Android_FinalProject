import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription,
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

export default function KhoaPage() {
  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);
  const [loading, setLoading] = useState(false);

  // Dialog
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<KhoaResponse | null>(null);

  // Form (chỉ có Tên khoa)
  const [tenKhoa, setTenKhoa] = useState("");

  // Search
  const [query, setQuery] = useState("");

  const token = useAuthStore((s) => s.token);

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

  const filtered = useMemo(() => {
    if (!query.trim()) return khoas;
    const q = query.toLowerCase().trim();
    return khoas.filter(
      (k) => (k.tenKhoa ?? "").toLowerCase().includes(q) || String(k.id).includes(q)
    );
  }, [khoas, query]);

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
      {/* Breadcrumb đơn giản */}
      <div className="text-sm text-gray-500">
        Trang chủ / Quản lý tổ chức / <span className="text-gray-700 font-medium">Quản lý khoa</span>
      </div>

      {/* Tiêu đề */}
      <h1 className="text-3xl font-bold text-center">Quản lý khoa</h1>

      {/* Thanh hành động: Thêm (trái) / Tìm kiếm (phải) */}
      <div className="flex items-center justify-between">
        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button onClick={openAddDialog} className="bg-[#457B9D] text-white hover:bg-[#3b6b86]">
              <Plus className="w-4 h-4 mr-2" /> Thêm khoa
            </Button>
          </DialogTrigger>

          {/* aria-describedby gắn với id ở dưới */}
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
                />
              </div>
            </div>

            <DialogFooter className="flex gap-2">
              <Button type="button" variant="secondary" className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]" onClick={() => setOpen(false)} disabled={submitting}>
                Trở về
              </Button>
              <Button type="button" className="bg-[#457B9D] text-white hover:bg-[#3b6b86]" onClick={handleSave} disabled={submitting}>
                {editing ? "Cập nhật" : "Tạo mới"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        {/* Search */}
        <div className="flex items-center gap-2">
          <Input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Tên khoa/Mã khoa"
            name="searchKhoa"
            autoComplete="off"
            className="w-64"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Bảng danh sách */}
      <div className="bg-white rounded-xl shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[20%]">Mã khoa</TableHead>
              <TableHead className="w-[60%]">Tên khoa</TableHead>
              <TableHead className="w-[20%] text-right">Hành động</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={3}>Đang tải...</TableCell>
              </TableRow>
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={3}>Không có dữ liệu</TableCell>
              </TableRow>
            ) : (
              filtered.map((khoa) => (
                <TableRow key={khoa.id}>
                  <TableCell className="font-medium">{khoa.id}</TableCell>
                  <TableCell>{khoa.tenKhoa}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(khoa)}
                      aria-label={`Sửa khoa ${khoa.tenKhoa}`}
                      title="Sửa"
                      type="button"
                    >
                      <Pencil className="w-4 h-4" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}

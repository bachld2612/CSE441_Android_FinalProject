// src/pages/LopPage.tsx
import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import { useAuthStore } from "@/stores/authStore";

// dùng list Ngành để render Select
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

  const token = useAuthStore((s) => s.token);

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
    } catch {
      /* im lặng */
    }
  };

  useEffect(() => {
    if (!token) return;
    fetchNganh();
    fetchLop();
  }, [token]);

  const nganhMap = useMemo(
    () => new Map(nganhs.map((n) => [n.id, n.tenNganh ?? ""])),
    [nganhs]
  );

  const filtered = useMemo(() => {
    if (!query.trim()) return items;
    const q = query.toLowerCase().trim();
    return items.filter((l) => {
      const name = (l.tenLop ?? "").toLowerCase();
      const idStr = String(l.id);
      const nName = (nganhMap.get(l.nganhId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || idStr.includes(q) || nName.includes(q);
    });
  }, [items, query, nganhMap]);

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
    if (!name) {
      toast.error("Tên lớp không được để trống");
      return;
    }
    if (!nganhId) {
      toast.error("Vui lòng chọn ngành");
      return;
    }

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
      // 401 từ interceptor sẽ tự đẩy về /login nếu token hết hạn/thiếu scope
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

      {/* Actions */}
      <div className="flex items-center justify-between">
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

          {/* Dialog ở giữa, nền trắng, có Description để hết cảnh báo a11y */}
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
                aria-label="Đóng hộp thoại"
                title="Đóng"
              >
                Trở về
              </Button>
              <Button
                type="button"
                className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                onClick={handleSave}
                disabled={submitting}
                aria-label={editing ? "Cập nhật lớp" : "Tạo mới lớp"}
                title={editing ? "Cập nhật lớp" : "Tạo mới lớp"}
              >
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
            placeholder="Tên lớp/Mã/Ngành"
            name="searchLop"
            autoComplete="off"
            className="w-64"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[15%]">Mã lớp</TableHead>
              <TableHead className="w-[45%]">Tên lớp</TableHead>
              <TableHead className="w-[20%]">Ngành</TableHead>
              <TableHead className="w-[20%] text-right">Hành động</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={4}>Đang tải...</TableCell>
              </TableRow>
            ) : filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4}>Không có dữ liệu</TableCell>
              </TableRow>
            ) : (
              filtered.map((l) => (
                <TableRow key={l.id}>
                  <TableCell className="font-medium">{l.id}</TableCell>
                  <TableCell>{l.tenLop}</TableCell>
                  <TableCell>{nganhMap.get(l.nganhId ?? -1) ?? ""}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(l)}
                      aria-label={`Sửa lớp ${l.tenLop ?? ""}`}
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

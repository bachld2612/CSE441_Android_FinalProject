// src/pages/NganhPage.tsx
import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription } from "@/components/ui/dialog";
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

  const token = useAuthStore((s) => s.token);

  const fetchNganh = async () => {
    setLoading(true);
    try {
      const res = await getAllNganh(); // ApiResponse<NganhResponse[]>
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
      // im lặng
    }
  };

  useEffect(() => {
    if (!token) return;
    fetchKhoa();
    fetchNganh();
  }, [token]);

  const khoaMap = useMemo(
    () => new Map(khoas.map((k) => [k.id, k.tenKhoa ?? ""])),
    [khoas]
  );

  const filtered = useMemo(() => {
    if (!query.trim()) return items;
    const q = query.toLowerCase().trim();
    return items.filter((n) => {
      const name = (n.tenNganh ?? "").toLowerCase();
      const idStr = String(n.id);
      const kName = (khoaMap.get(n.khoaId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || idStr.includes(q) || kName.includes(q);
    });
  }, [items, query, khoaMap]);

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
    if (!name) {
      toast.error("Tên ngành không được để trống");
      return;
    }
    if (!khoaId) {
      toast.error("Vui lòng chọn khoa");
      return;
    }

    setSubmitting(true);
    try {
      const payload: NganhRequest = { tenNganh: name, khoaId };
      const res = editing
        ? await updateNganh(editing.id, payload)
        : await createNganh(payload);

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
    } catch (e: unknown) {
      // Nếu token hết hạn hoặc thiếu quyền, interceptor sẽ 401 và điều hướng /login
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

      {/* Actions */}
      <div className="flex items-center justify-between">
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

          {/* Dialog giữa màn hình; thêm Description để hết cảnh báo a11y */}
          <DialogContent
            className="sm:max-w-md bg-white"
            aria-describedby="nganh-dialog-desc"
          >
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
                  placeholder="Ví dụ: Ngành Công nghệ thông tin"
                  onKeyDown={(e) => { if (e.key === "Enter") handleSave(); }}
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="khoaSelect">Khoa</Label>
                <Select
                  value={khoaId ? String(khoaId) : ""}
                  onValueChange={(v) => setKhoaId(Number(v))}
                >
                  {/* Custom style để đồng bộ với dialog */}
                  <SelectTrigger
                    id="khoaSelect"
                    aria-label="Chọn khoa"
                    className="
                      h-10 rounded-xl
                      border border-gray-300
                      bg-white text-gray-900
                      hover:bg-gray-50
                      focus:outline-none focus:ring-2 focus:ring-[#457B9D]
                      data-[state=open]:ring-2 data-[state=open]:ring-[#457B9D]
                    "
                  >
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
                aria-label={editing ? "Cập nhật ngành" : "Tạo mới ngành"}
                title={editing ? "Cập nhật ngành" : "Tạo mới ngành"}
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
            placeholder="Tên ngành/Mã/Khoa"
            name="searchNganh"
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
              <TableHead className="w-[15%]">Mã ngành</TableHead>
              <TableHead className="w-[45%]">Tên ngành</TableHead>
              <TableHead className="w-[20%]">Khoa</TableHead>
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
              filtered.map((n) => (
                <TableRow key={n.id}>
                  <TableCell className="font-medium">{n.id}</TableCell>
                  <TableCell>{n.tenNganh}</TableCell>
                  <TableCell>{khoaMap.get(n.khoaId ?? -1) ?? ""}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(n)}
                      aria-label={`Sửa ngành ${n.tenNganh ?? ""}`}
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

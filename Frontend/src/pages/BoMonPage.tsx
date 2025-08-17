import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search } from "lucide-react";
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
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogDescription,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import {
  getAllBoMon,
  createBoMon,
  updateBoMon,
  type BoMonResponse,
  type BoMonRequest,
} from "@/services/bo-mon.service";
import { getAllKhoa, type KhoaResponse } from "@/services/khoa.service";
import { useAuthStore } from "@/stores/authStore";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";

export default function BoMonPage() {
  const [items, setItems] = useState<BoMonResponse[]>([]);
  const [loading, setLoading] = useState(false);

  // Danh sách khoa để render Select
  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);

  // Dialog
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<BoMonResponse | null>(null);

  // Form
  const [tenBoMon, setTenBoMon] = useState("");
  const [khoaId, setKhoaId] = useState<number | null>(null);

  // Search
  const [query, setQuery] = useState("");

  const token = useAuthStore((s) => s.token);

  const fetchBoMon = async () => {
    setLoading(true);
    try {
      const res = await getAllBoMon(); // ApiResponse<BoMonResponse[]>
      setItems(res.result ?? []);
    } catch {
      toast.error("Không thể tải danh sách bộ môn");
    } finally {
      setLoading(false);
    }
  };

  const fetchKhoa = async () => {
    try {
      const res = await getAllKhoa();
      setKhoas(res.result ?? []);
    } catch {
      // bỏ qua toast
    }
  };

  useEffect(() => {
    if (!token) return;
    fetchKhoa();
    fetchBoMon();
  }, [token]);

  const khoaMap = useMemo(
    () => new Map(khoas.map((k) => [k.id, k.tenKhoa ?? ""])),
    [khoas]
  );

  const filtered = useMemo(() => {
    if (!query.trim()) return items;
    const q = query.toLowerCase().trim();
    return items.filter((b) => {
      const name = (b.tenBoMon ?? "").toLowerCase();
      const idStr = String(b.id);
      const kName = (khoaMap.get(b.khoaId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || idStr.includes(q) || kName.includes(q);
    });
  }, [items, query, khoaMap]);

  const openAddDialog = () => {
    setEditing(null);
    setTenBoMon("");
    setKhoaId(null);
    setOpen(true);
  };

  const openEditDialog = (b: BoMonResponse) => {
    setEditing(b);
    setTenBoMon(b.tenBoMon ?? "");
    setKhoaId(b.khoaId ?? null);
    setOpen(true);
  };

  const handleSave = async () => {
    const name = tenBoMon.trim();
    if (!name) {
      toast.error("Tên bộ môn không được để trống");
      return;
    }
    if (!khoaId) {
      toast.error("Vui lòng chọn khoa");
      return;
    }

    setSubmitting(true);
    try {
      const payload: BoMonRequest = { tenBoMon: name, khoaId };
      const res = editing
        ? await updateBoMon(editing.id, payload)
        : await createBoMon(payload);

      if (res.result) {
        toast.success(editing ? "Cập nhật bộ môn thành công" : "Thêm bộ môn thành công");
        setOpen(false);
        setEditing(null);
        setTenBoMon("");
        setKhoaId(null);
        fetchBoMon();
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
        Trang chủ / Quản lý tổ chức /{" "}
        <span className="text-gray-700 font-medium">Quản lý bộ môn</span>
      </div>

      {/* Title */}
      <h1 className="text-3xl font-bold text-center">Quản lý bộ môn</h1>

      {/* Actions */}
      <div className="flex items-center justify-between">
        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button
              onClick={openAddDialog}
              className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
              aria-label="Thêm bộ môn"
              title="Thêm bộ môn"
              type="button"
            >
              <Plus className="w-4 h-4 mr-2" />
              Thêm bộ môn
            </Button>
          </DialogTrigger>

          {/* Ép nền trắng cho panel dialog ngay tại đây */}
          <DialogContent
            className="sm:max-w-md bg-white text-gray-900"
            aria-describedby="bomon-dialog-desc"
          >
            <DialogHeader>
              <DialogTitle>{editing ? "Sửa bộ môn" : "Thêm bộ môn"}</DialogTitle>
              <DialogDescription id="bomon-dialog-desc" className="sr-only">
                Biểu mẫu {editing ? "cập nhật" : "tạo mới"} bộ môn.
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-4">
              <div className="grid gap-2">
                <Label htmlFor="tenBoMonInput">Tên bộ môn</Label>
                <Input
                  id="tenBoMonInput"
                  name="tenBoMon"
                  autoComplete="off"
                  value={tenBoMon}
                  onChange={(e) => setTenBoMon(e.target.value)}
                  placeholder="Ví dụ: Bộ môn Công nghệ phần mềm"
                  onKeyDown={(e) => {
                    if (e.key === "Enter") handleSave();
                  }}
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="khoaSelect">Khoa</Label>

                <Select
                  value={khoaId ? String(khoaId) : ""}
                  onValueChange={(v) => setKhoaId(Number(v))}
                >
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
                    className="
                      bg-white text-gray-900
                      border border-gray-200 rounded-xl shadow-lg
                      overflow-hidden
                    "
                  >
                    {khoas.map((k) => (
                      <SelectItem
                        key={k.id}
                        value={String(k.id)}
                        className="
                          cursor-pointer
                          focus:bg-[#EFF6FF] focus:text-[#006EFF]
                          data-[highlighted]:bg-[#EFF6FF] data-[highlighted]:text-[#006EFF]
                        "
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
                aria-label={editing ? "Cập nhật bộ môn" : "Tạo mới bộ môn"}
                title={editing ? "Cập nhật bộ môn" : "Tạo mới bộ môn"}
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
            placeholder="Tên bộ môn/Mã/Khoa"
            name="searchBoMon"
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
              <TableHead className="w-[15%]">Mã bộ môn</TableHead>
              <TableHead className="w-[45%]">Tên bộ môn</TableHead>
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
              filtered.map((b) => (
                <TableRow key={b.id}>
                  <TableCell className="font-medium">{b.id}</TableCell>
                  <TableCell>{b.tenBoMon}</TableCell>
                  <TableCell>{khoaMap.get(b.khoaId ?? -1) ?? ""}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(b)}
                      aria-label={`Sửa bộ môn ${b.tenBoMon ?? ""}`}
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

// src/pages/BoMonPage.tsx
import { useEffect, useMemo, useState } from "react";
import { Pencil, Plus, Search, UserPlus2 } from "lucide-react";
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
  getAllBoMon, createBoMon, updateBoMon,
  createTruongBoMon,
  type BoMonResponse, type BoMonRequest,
  type TruongBoMonCreationRequest,
} from "@/services/bo-mon.service";
import { getAllKhoa, type KhoaResponse } from "@/services/khoa.service";
import { getGiangVienByBoMon, type GiangVienLite } from "@/services/giang-vien.service";
import { useAuthStore } from "@/stores/authStore";
import {
  Select, SelectTrigger, SelectValue, SelectContent, SelectItem,
} from "@/components/ui/select";

export default function BoMonPage() {
  const [items, setItems] = useState<BoMonResponse[]>([]);
  const [loading, setLoading] = useState(false);

  // Khoa
  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);

  // Dialog bộ môn
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<BoMonResponse | null>(null);
  const [tenBoMon, setTenBoMon] = useState("");
  const [khoaId, setKhoaId] = useState<number | null>(null);

  // Dialog Trưởng bộ môn
  const [openTBM, setOpenTBM] = useState(false);
  const [tbmBoMonId, setTbmBoMonId] = useState<number | null>(null);
  const [tbmTeacherId, setTbmTeacherId] = useState<number | null>(null);
  const [tbmTeachers, setTbmTeachers] = useState<GiangVienLite[]>([]);
  const [tbmLoading, setTbmLoading] = useState(false);

  // Search
  const [query, setQuery] = useState("");
  const token = useAuthStore((s) => s.token);

  const fetchBoMon = async () => {
    setLoading(true);
    try {
      const res = await getAllBoMon();
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
    } catch {}
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
    if (!name) return toast.error("Tên bộ môn không được để trống");
    if (!khoaId) return toast.error("Vui lòng chọn khoa");

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
    } catch (e: any) {
      toast.error(e?.response?.data?.message || "Không thể lưu thông tin");
    } finally {
      setSubmitting(false);
    }
  };

  // ===== Trưởng bộ môn =====
  const openAddTBM = () => {
    setTbmBoMonId(null);
    setTbmTeacherId(null);
    setTbmTeachers([]);
    setOpenTBM(true);
  };

  const loadTeachers = async (boMonId: number) => {
    setTbmLoading(true);
    try {
      const res = await getGiangVienByBoMon(boMonId);
      setTbmTeachers(res.result ?? []);
      setTbmTeacherId(null);
      if ((res.result ?? []).length === 0) {
        toast.info("Bộ môn này chưa có giảng viên hoặc không tìm thấy.");
      }
    } catch {
      toast.error("Không thể tải danh sách giảng viên");
      setTbmTeachers([]);
    } finally {
      setTbmLoading(false);
    }
  };

  const handleChangeBoMonTBM = (value: string) => {
    const id = Number(value);
    setTbmBoMonId(id);
    if (id) loadTeachers(id);
  };

  const handleSaveTBM = async () => {
    if (!tbmBoMonId) return toast.error("Vui lòng chọn bộ môn");
    if (!tbmTeacherId) return toast.error("Vui lòng chọn giảng viên");

    try {
      const payload: TruongBoMonCreationRequest = {
        boMonId: tbmBoMonId,
        giangVienId: tbmTeacherId,
      };
      const res = await createTruongBoMon(payload);
      if (res.result) {
        toast.success(`Đã đặt ${res.result.hoTen} làm Trưởng bộ môn ${res.result.tenBoMon}`);
        setOpenTBM(false);
        fetchBoMon();
      } else {
        toast.error(res.message || "Thao tác thất bại");
      }
    } catch (e: any) {
      toast.error(e?.response?.data?.message || "Không thể tạo trưởng bộ môn");
    }
  };

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500">
        Trang chủ / Quản lý tổ chức / <span className="text-gray-700 font-medium">Quản lý bộ môn</span>
      </div>

      <h1 className="text-3xl font-bold text-center">Quản lý bộ môn</h1>

      {/* Actions */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          {/* Thêm/Sửa bộ môn */}
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

            <DialogContent className="sm:max-w-md bg-white text-gray-900" aria-describedby="bomon-desc">
              <DialogHeader>
                <DialogTitle>{editing ? "Sửa bộ môn" : "Thêm bộ môn"}</DialogTitle>
                <DialogDescription id="bomon-desc" className="sr-only">
                  Biểu mẫu {editing ? "cập nhật" : "tạo mới"} bộ môn.
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-4">
                <div className="grid gap-2">
                  <Label htmlFor="tenBoMonInput">Tên bộ môn</Label>
                  <Input
                    id="tenBoMonInput"
                    value={tenBoMon}
                    onChange={(e) => setTenBoMon(e.target.value)}
                    autoComplete="off"
                    placeholder="Ví dụ: Bộ môn Công nghệ phần mềm"
                    onKeyDown={(e) => e.key === "Enter" && handleSave()}
                  />
                </div>

                <div className="grid gap-2">
                  <Label htmlFor="khoaSelect">Khoa</Label>
                  <Select value={khoaId ? String(khoaId) : ""} onValueChange={(v) => setKhoaId(Number(v))}>
                    <SelectTrigger id="khoaSelect" aria-label="Chọn khoa" className="h-10 rounded-xl border border-gray-300 bg-white">
                      <SelectValue placeholder="Chọn khoa" />
                    </SelectTrigger>
                    <SelectContent className="bg-white border rounded-xl shadow-lg">
                      {khoas.map((k) => (
                        <SelectItem key={k.id} value={String(k.id)}>
                          {k.tenKhoa}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <DialogFooter className="flex gap-2">
                <Button variant="secondary" className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]" onClick={() => setOpen(false)}>
                  Trở về
                </Button>
                <Button className="bg-[#457B9D] text-white hover:bg-[#3b6b86]" onClick={handleSave}>
                  {editing ? "Cập nhật" : "Tạo mới"}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          {/* Thêm Trưởng bộ môn */}
          <Dialog open={openTBM} onOpenChange={setOpenTBM}>
            <DialogTrigger asChild>
              <Button
                onClick={openAddTBM}
                variant="outline"
                className="border-[#457B9D] text-[#457B9D] hover:bg-[#ebf3f7]"
                aria-label="Thêm trưởng bộ môn"
                title="Thêm trưởng bộ môn"
                type="button"
              >
                <UserPlus2 className="w-4 h-4 mr-2" />
                Thêm trưởng bộ môn
              </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-md bg-white text-gray-900" aria-describedby="tbm-desc">
              <DialogHeader>
                <DialogTitle>Thêm trưởng bộ môn</DialogTitle>
                <DialogDescription id="tbm-desc" className="text-sm text-gray-500">
                  Chọn <b>Bộ môn</b> trước, sau đó chọn <b>Giảng viên</b> thuộc bộ môn đó.
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-4">
                {/* Chọn bộ môn */}
                <div className="grid gap-2">
                  <Label htmlFor="tbmBoMonSelect">Bộ môn</Label>
                  <Select
                    value={tbmBoMonId ? String(tbmBoMonId) : ""}
                    onValueChange={handleChangeBoMonTBM}
                  >
                    <SelectTrigger id="tbmBoMonSelect" aria-label="Chọn bộ môn" className="h-10 rounded-xl border border-gray-300 bg-white">
                      <SelectValue placeholder="Chọn bộ môn" />
                    </SelectTrigger>
                    <SelectContent className="bg-white border rounded-xl shadow-lg">
                      {items.map((b) => (
                        <SelectItem key={b.id} value={String(b.id)}>
                          {b.tenBoMon}
                          {khoaMap.get(b.khoaId ?? -1) ? ` — ${khoaMap.get(b.khoaId ?? -1)}` : ""}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Chọn giảng viên theo bộ môn */}
                <div className="grid gap-2">
                  <Label htmlFor="tbmTeacherSelect">Giảng viên</Label>
                  <Select
                    value={tbmTeacherId ? String(tbmTeacherId) : ""}
                    onValueChange={(v) => setTbmTeacherId(Number(v))}
                    disabled={!tbmBoMonId || tbmLoading || tbmTeachers.length === 0}
                  >
                    <SelectTrigger id="tbmTeacherSelect" aria-label="Chọn giảng viên" className="h-10 rounded-xl border border-gray-300 bg-white disabled:opacity-60">
                      <SelectValue
                        placeholder={
                          tbmLoading
                            ? "Đang tải giảng viên…"
                            : !tbmBoMonId
                            ? "Chọn bộ môn trước"
                            : tbmTeachers.length === 0
                            ? "Không có giảng viên"
                            : "Chọn giảng viên"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent className="bg-white border rounded-xl shadow-lg">
                      {tbmTeachers.map((gv) => (
                        <SelectItem key={gv.id} value={String(gv.id)}>
                          {gv.maGV} — {gv.hoTen}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <DialogFooter className="flex gap-2">
                <Button variant="secondary" className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]" onClick={() => setOpenTBM(false)}>
                  Trở về
                </Button>
                <Button className="bg-[#457B9D] text-white hover:bg-[#3b6b86]" onClick={handleSaveTBM} disabled={!tbmBoMonId || !tbmTeacherId}>
                  Xác nhận
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

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
              <TableHead className="w-[45%]">Tên bộ môn</TableHead>
              <TableHead className="w-[35%]">Khoa</TableHead>
              <TableHead className="w-[20%] text-center">Hành động</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={4}>Đang tải...</TableCell></TableRow>
            ) : filtered.length === 0 ? (
              <TableRow><TableCell colSpan={4}>Không có dữ liệu</TableCell></TableRow>
            ) : filtered.map((b) => (
              <TableRow key={b.id}>
                <TableCell>{b.tenBoMon}</TableCell>
                <TableCell>{khoaMap.get(b.khoaId ?? -1) ?? ""}</TableCell>
                <TableCell className="text-center">
                  <Button size="icon" variant="outline" onClick={() => openEditDialog(b)} title="Sửa" type="button">
                    <Pencil className="w-4 h-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}

// src/pages/BoMonPage.tsx
import { useEffect, useMemo, useState } from "react";
import { ChevronLeft, ChevronRight, Pencil, Plus, Search, UserPlus2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger, DialogDescription } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import {
  // dùng endpoint có phân trang
  getBoMonWithTBMPage,
  createBoMon, updateBoMon, createTruongBoMon,
  type BoMonResponse, type BoMonRequest,
  type TruongBoMonCreationRequest,
  type BoMonWithTruongBoMonResponse,
} from "@/services/bo-mon.service";
import { getAllKhoa, type KhoaResponse } from "@/services/khoa.service";
import { getGiangVienByBoMon, type GiangVienLite } from "@/services/giang-vien.service";
import { useAuthStore } from "@/stores/authStore";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb";

export default function BoMonPage() {
  // dữ liệu bảng theo trang
  const [items, setItems] = useState<BoMonResponse[]>([]);
  const [loading, setLoading] = useState(false);

  // phân trang
  const [page, setPage] = useState(0);      // 0-based
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  // Map bộ môn -> tên TBM hiện tại (từ content trả về)
  const [tbmMap, setTbmMap] = useState<Map<number, string | null>>(new Map());

  // Khoa (để hiển thị tên khoa)
  const [khoas, setKhoas] = useState<KhoaResponse[]>([]);

  // Dialog bộ môn (thêm/sửa)
  const [open, setOpen] = useState(false);
  const [, setSubmitting] = useState(false);
  const [editing, setEditing] = useState<BoMonResponse | null>(null);
  const [tenBoMon, setTenBoMon] = useState("");
  const [khoaId, setKhoaId] = useState<number | null>(null);

  // Dialog Trưởng bộ môn
  const [openTBM, setOpenTBM] = useState(false);
  const [tbmBoMonId, setTbmBoMonId] = useState<number | null>(null);
  const [tbmTeacherId, setTbmTeacherId] = useState<number | null>(null);
  const [tbmTeachers, setTbmTeachers] = useState<GiangVienLite[]>([]);
  const [tbmLoading, setTbmLoading] = useState(false);

  // Search (client-side trên trang hiện tại)
  const [query, setQuery] = useState("");

  // Auth
  const token = useAuthStore((s) => s.token);
  const [role, setRole] = useState<string | null>(null);
  const canManage = role === "ADMIN" || role === "TRO_LY_KHOA";

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

  const fetchKhoa = async () => {
    try {
      const res = await getAllKhoa();
      setKhoas(res.result ?? []);
    } catch (e){
      console.error("Error fetching khoa:", e);
    }
  };

  // tải 1 trang bộ môn (kèm tên TBM trong content)
  const fetchPage = async (p = page, s = size) => {
    setLoading(true);
    try {
      const res = await getBoMonWithTBMPage({ page: p, size: s, sort: "tenBoMon,ASC" });
      const content = (res.result?.content ?? []) as BoMonWithTruongBoMonResponse[];

      // items chỉ cần id/tenBoMon/khoaId để render bảng
      const rows: BoMonResponse[] = content.map((b) => ({
        id: b.id, tenBoMon: b.tenBoMon, khoaId: b.khoaId,
      }));
      setItems(rows);

      // map TBM cho trang hiện tại
      const m = new Map<number, string | null>();
      content.forEach((b) => m.set(b.id, b.truongBoMonHoTen ?? null));
      setTbmMap(m);

      setTotalPages(res.result?.totalPages ?? 0);
    } catch {
      toast.error("Không thể tải danh sách bộ môn");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!token) return;
    fetchKhoa();
    fetchPage(0, size);
    setPage(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  // đổi trang/kích thước
  useEffect(() => {
    if (!token) return;
    fetchPage(page, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  const khoaMap = useMemo(
    () => new Map(khoas.map((k) => [k.id, k.tenKhoa ?? ""])),
    [khoas]
  );

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return items;
    return items.filter((b) => {
      const name = (b.tenBoMon ?? "").toLowerCase();
      const kName = (khoaMap.get(b.khoaId ?? -1) ?? "").toLowerCase();
      return name.includes(q) || kName.includes(q);
    });
  }, [items, query, khoaMap]);

  // ===== Dialog thêm/sửa bộ môn =====
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

  const handleSaveBoMon = async () => {
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
        fetchPage(page, size); // reload trang hiện tại
      } else {
        toast.error(res.message || "Thao tác thất bại");
      }
    } catch (e: any) {
      toast.error(e?.response?.data?.message || "Không thể lưu thông tin");
    } finally {
      setSubmitting(false);
    }
  };

  // ===== Dialog Trưởng bộ môn =====
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

  const openAddTBM = () => {
    setTbmBoMonId(null);
    setTbmTeacherId(null);
    setTbmTeachers([]);
    setOpenTBM(true);
  };

  const openTBMForBoMon = (boMonId: number) => {
    if (!canManage) return;
    setTbmBoMonId(boMonId);
    setOpenTBM(true);
    loadTeachers(boMonId);
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
        toast.success(`Đã cập nhật Trưởng bộ môn: ${res.result.hoTen} (${res.result.tenBoMon})`);
        setOpenTBM(false);
        // cập nhật lại map TBM của trang hiện tại
        fetchPage(page, size);
      } else {
        toast.error(res.message || "Thao tác thất bại");
      }
    } catch (e: any) {
      toast.error(e?.response?.data?.message || "Không thể tạo trưởng bộ môn");
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
              Bộ Môn
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl font-bold text-center">Quản lý bộ môn</h1>

      {/* Actions */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          {canManage && (
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
                      onKeyDown={(e) => e.key === "Enter" && handleSaveBoMon()}
                      className="border border-gray-300"
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
                  <Button className="bg-[#457B9D] text-white hover:bg-[#3b6b86]" onClick={handleSaveBoMon}>
                    {editing ? "Cập nhật" : "Tạo mới"}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}

          {canManage && (
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
                  <DialogTitle>Trưởng bộ môn</DialogTitle>
                  <DialogDescription id="tbm-desc" className="text-sm text-gray-500" />
                </DialogHeader>

                <div className="space-y-4">
                  {/* Chọn bộ môn */}
                  <div className="grid gap-1">
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
                    {tbmBoMonId && (
                      <p className="text-xs text-gray-600 mt-1">
                        Trưởng bộ môn hiện tại:{" "}
                        <span className="font-medium">
                          {tbmMap.get(tbmBoMonId!) || "Chưa phân công"}
                        </span>
                      </p>
                    )}
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
                            {gv.hoTen}
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
                  <Button
                    className="bg-[#457B9D] text-white hover:bg-[#3b6b86]"
                    onClick={handleSaveTBM}
                    disabled={!tbmBoMonId || !tbmTeacherId}
                  >
                    Xác nhận
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        {/* Search (style như trang Giảng viên) */}
        <div className="flex items-center gap-2">
          <Input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Tên bộ môn/Khoa"
            name="searchBoMon"
            autoComplete="off"
            className="w-64 border border-gray-300"
          />
          <Button variant="outline" size="icon" aria-label="Tìm kiếm" title="Tìm kiếm" className="border border-gray-300">
            <Search className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Bảng + Phân trang giống Giảng viên */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300 w-[40%]">
              Bộ môn
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300 w-[40%]">
              Khoa
            </TableHead>
            {canManage && (
              <TableHead className="text-center font-semibold border border-gray-300 w-[20%]">
                Hành động
              </TableHead>
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
          ) : filtered.length === 0 ? (
            <TableRow>
              <TableCell className="text-center border border-gray-300" colSpan={canManage ? 3 : 2}>
                Không có dữ liệu
              </TableCell>
            </TableRow>
          ) : (
            filtered.map((b) => (
              <TableRow key={b.id} className="hover:bg-gray-50 transition-colors">
                <TableCell className="text-center border border-gray-300">
                  {canManage ? (
                    <button
                      onClick={() => openTBMForBoMon(b.id)}
                      className="underline underline-offset-2 decoration-dotted hover:text-[#457B9D]"
                      title="Xem/đặt Trưởng bộ môn"
                      type="button"
                    >
                      {b.tenBoMon}
                    </button>
                  ) : (
                    <span>{b.tenBoMon}</span>
                  )}
                  <div className="text-xs text-gray-500 mt-1">
                    TBM: <span className="font-medium">{tbmMap.get(b.id) || "Chưa phân công"}</span>
                  </div>
                </TableCell>

                <TableCell className="text-center border border-gray-300">
                  {khoaMap.get(b.khoaId ?? -1) ?? ""}
                </TableCell>

                {canManage && (
                  <TableCell className="text-center border border-gray-300">
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => openEditDialog(b)}
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

      {/* Pagination (giống Giảng viên) */}
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

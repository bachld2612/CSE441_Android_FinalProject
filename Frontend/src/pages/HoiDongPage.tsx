// src/pages/HoiDongPage.tsx
import { useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";
import api from "@/lib/axios";

import {
  getHoiDongPage,
  createHoiDong,
  getHoiDongDetail,
  importSinhVienToHoiDong,
} from "@/services/hoiDong.service";

import {
  getDotBaoVeOptions,
  getDotBaoVeIdBy,
  type DotBaoVeOption,
} from "@/services/dotBaoVe.service";

import type {
  HoiDongListItem,
  HoiDongDetail,
  ImportResult,
  HoiDongType,
} from "@/types/hoiDong.types";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  ChevronLeft,
  ChevronRight,
  Eye,
  Search,
  Upload,
  X,
  Plus,
  Trash2,
  CheckCircle,
  XCircle,
  Loader2,
} from "lucide-react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

/* ===== Utils ===== */
const fmt = (d?: string) => (d ? new Date(d).toLocaleDateString("vi-VN") : "-");
const toVnLoai = (x?: string) =>
  x === "DEFENSE" ? "Bảo vệ" : x === "PEER_REVIEW" ? "Phản biện" : x ?? "-";

/* tải file log (cố đặt tên cố định, có fallback) */
async function downloadByUrl(url: string, filename = "ket_qua_import.xlsx") {
  try {
    const res = await fetch(url, { mode: "cors" });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const blob = await res.blob();
    const blobUrl = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = blobUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(blobUrl);
  } catch {
    const a = document.createElement("a");
    a.href = url;
    a.setAttribute("download", filename);
    document.body.appendChild(a);
    a.click();
    a.remove();
  }
}

type Props = { fixedLoai: HoiDongType; title: string };

type GiangVienOption = {
  id: number;
  hoTen: string;
  maGV?: string;
  boMon?: string;
  email?: string;
};

/* ===== Ô nhập GV – lọc client theo chữ người dùng gõ ===== */
function SuggestGVInput({
  value,
  onSelect,
  placeholder,
  source,
}: {
  value: GiangVienOption | null;
  onSelect: (opt: GiangVienOption | null) => void;
  placeholder?: string;
  source: GiangVienOption[];
}) {
  const [q, setQ] = useState(
    value ? `${value.hoTen}${value.maGV ? ` (${value.maGV})` : ""}` : ""
  );
  const [open, setOpen] = useState(false);
  const [hi, setHi] = useState(0);
  const wrapRef = useRef<HTMLDivElement>(null);

  const norm = (s: string) =>
    (s || "")
      .toLowerCase()
      .normalize("NFD")
      .replace(/\p{Diacritic}/gu, "");
  const filtered = source
    .filter((x) => {
      const hay = `${x.hoTen} ${x.maGV ?? ""} ${x.boMon ?? ""}`;
      return norm(hay).includes(norm(q));
    })
    .slice(0, 20);

  useEffect(() => {
    const fn = (e: MouseEvent) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node))
        setOpen(false);
    };
    document.addEventListener("mousedown", fn);
    return () => document.removeEventListener("mousedown", fn);
  }, []);

  return (
    <div ref={wrapRef} className="relative">
      <Input
        value={q}
        onChange={(e) => {
          setQ(e.target.value);
          setOpen(true);
          if (value) onSelect(null);
        }}
        onFocus={() => setOpen(true)}
        onKeyDown={(e) => {
          if (!open || filtered.length === 0) return;
          if (e.key === "ArrowDown") {
            e.preventDefault();
            setHi((h) => (h + 1) % filtered.length);
          } else if (e.key === "ArrowUp") {
            e.preventDefault();
            setHi((h) => (h - 1 + filtered.length) % filtered.length);
          } else if (e.key === "Enter") {
            e.preventDefault();
            const chosen = filtered[hi];
            if (chosen) {
              onSelect(chosen);
              setQ(`${chosen.hoTen}${chosen.maGV ? ` (${chosen.maGV})` : ""}`);
              setOpen(false);
            }
          }
        }}
        placeholder={placeholder ?? "Nhập tên/mã GV để tìm..."}
        className="pr-9"
      />

      {q && (
        <button
          type="button"
          onClick={() => {
            setQ("");
            onSelect(null);
            setOpen(true);
          }}
          className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
        >
          <X className="w-4 h-4" />
        </button>
      )}

      {open && (
        <div className="absolute left-0 right-0 top-full z-40 mt-1 rounded-md border bg-white shadow-md max-h-64 overflow-auto">
          {filtered.length === 0 ? (
            <div className="px-3 py-2 text-sm text-gray-500">
              Không có gợi ý
            </div>
          ) : (
            filtered.map((o, idx) => {
              const label = `${o.hoTen}${o.maGV ? ` (${o.maGV})` : ""}${
                o.boMon ? ` · ${o.boMon}` : ""
              }`;
              return (
                <button
                  key={o.id + "-" + idx}
                  type="button"
                  onClick={() => {
                    onSelect(o);
                    setQ(`${o.hoTen}${o.maGV ? ` (${o.maGV})` : ""}`);
                    setOpen(false);
                  }}
                  className={`w-full text-left px-3 py-2 text-sm hover:bg-gray-100 ${
                    idx === hi ? "bg-gray-50" : ""
                  }`}
                >
                  {label}
                </button>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}

/* ================== PAGE ================== */
export default function HoiDongPage({ fixedLoai, title }: Props) {
  const [userRole, setUserRole] = useState<string | null>(null);

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        setUserRole(parsedInfo.role || null);
      } catch (error) {
        console.error("Lỗi parse myInfo:", error);
      }
    }
  }, []);

  const [searchParams] = useSearchParams();

  /* ====== DOT FILTER (ngoài danh sách) ====== */
  const [dotOptions, setDotOptions] = useState<DotBaoVeOption[]>([]);
  const [dotId, setDotId] = useState<number | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const opts = await getDotBaoVeOptions();
        setDotOptions(opts);
        const fromParam = Number(searchParams.get("dot"));
        if (fromParam) setDotId(fromParam);
        else if (opts.length) setDotId(opts[0].value);
      } catch {
        setDotOptions([]);
        setDotId(null);
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // list
  const [rows, setRows] = useState<HoiDongListItem[]>([]);
  const [page, setPage] = useState(0);
  const size = 7;
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // search
  const [q, setQ] = useState("");

  // dialogs
  const [openCreate, setOpenCreate] = useState(false);
  const [detailId, setDetailId] = useState<number | null>(null);
  const [detail, setDetail] = useState<HoiDongDetail | null>(null);

  // import
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [importHoiDongId, setImportHoiDongId] = useState<number | null>(null);

  const loadData = async () => {
    if (!dotId) return;
    setLoading(true);
    try {
      const data = await getHoiDongPage({
        page,
        size,
        q,
        loai: fixedLoai,
        dotBaoVeId: dotId,
      } as any);
      setRows(data.content);
      setTotalPages(data.totalPages);
    } catch {
      toast.error("Không thể tải danh sách hội đồng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, fixedLoai, dotId]);

  /* ====== CREATE ====== */
  const [tenHoiDong, setTenHoiDong] = useState("");
  const [ngayBatDau, setNgayBatDau] = useState("");
  const [ngayKetThuc, setNgayKetThuc] = useState("");

  const [chuTich, setChuTich] = useState<GiangVienOption | null>(null);
  const [thuKy, setThuKy] = useState<GiangVienOption | null>(null);

  const [reviewers, setReviewers] = useState<Array<GiangVienOption | null>>([
    null,
  ]);

  // preload GV
  const [allGV, setAllGV] = useState<GiangVienOption[]>([]);
  const [loadingGV, setLoadingGV] = useState(false);
  useEffect(() => {
    if (!openCreate) return;
    if (allGV.length > 0) return;
    (async () => {
      setLoadingGV(true);
      try {
        const res = await api.get("/giang-vien/list", {
          params: { keyword: "", page: 0, size: 1000 },
        });
        const payload = (res as any).data?.result ?? (res as any).result ?? {};
        const list = Array.isArray(payload?.content)
          ? payload.content
          : payload;
        const mapped: GiangVienOption[] = (list as any[])
          .map((x) => ({
            id: x.id ?? x.giangVienId ?? x.userId ?? x.maGV ?? 0,
            hoTen: x.hoTen ?? x.fullName ?? x.ten ?? x.name ?? "",
            maGV: x.maGV ?? x.code ?? "",
            boMon:
              (x.boMon && (x.boMon.ten ?? x.boMon.name)) ??
              x.boMon ??
              x.boMonName ??
              "",
            email: x.email ?? "",
          }))
          .filter((x) => x.id && x.hoTen);
        setAllGV(mapped);
      } catch {
        setAllGV([]);
      } finally {
        setLoadingGV(false);
      }
    })();
  }, [openCreate, allGV.length]);

  /* ====== SUY RA ĐỢT (trong modal tạo) ====== */
  const [namBatDau, setNamBatDau] = useState<string>("");
  const [hocKi, setHocKi] = useState<string>("");
  const [dotThu, setDotThu] = useState<string>("");
  const [dotIdCreate, setDotIdCreate] = useState<number | null>(null);
  const [findingDot, setFindingDot] = useState(false);

  useEffect(() => {
    (async () => {
      const y = Number(namBatDau);
      const hk = Number(hocKi);
      const dt = Number(dotThu);
      if (!y || !hk || !dt) {
        setDotIdCreate(null);
        return;
      }
      setFindingDot(true);
      try {
        const id = await getDotBaoVeIdBy({
          namBatDau: y,
          hocKi: hk,
          dotThu: dt,
        });
        setDotIdCreate(id);
      } catch {
        setDotIdCreate(null);
      } finally {
        setFindingDot(false);
      }
    })();
  }, [namBatDau, hocKi, dotThu]);

  const creating = useMemo(
    () =>
      !tenHoiDong ||
      !ngayBatDau ||
      !ngayKetThuc ||
      !chuTich ||
      !dotIdCreate ||
      (fixedLoai === "DEFENSE" && !thuKy),
    [
      tenHoiDong,
      ngayBatDau,
      ngayKetThuc,
      chuTich,
      thuKy,
      fixedLoai,
      dotIdCreate,
    ]
  );

  const addReviewer = () => setReviewers((arr) => [...arr, null]);
  const removeReviewer = (idx: number) =>
    setReviewers((arr) => arr.filter((_, i) => i !== idx));
  const setReviewerAt = (idx: number, v: GiangVienOption | null) =>
    setReviewers((arr) => arr.map((x, i) => (i === idx ? v : x)));

  const ROLE = {
    CHU_TICH: "CHAIR",
    THU_KY: "SECRETARY",
    PHAN_BIEN: "EXAMINER",
  };

  const doCreate = async () => {
    if (!dotIdCreate) {
      toast.error("Chưa xác định được đợt. Nhập đủ Năm/Học kỳ/Đợt thứ.");
      return;
    }

    // map giảng viên -> lecturers
    const lecturers: Array<{ giangVienId: number; role: string }> = [];
    if (chuTich?.id)
      lecturers.push({ giangVienId: chuTich.id, role: ROLE.CHU_TICH });
    if (fixedLoai === "DEFENSE" && thuKy?.id)
      lecturers.push({ giangVienId: thuKy.id, role: ROLE.THU_KY });
    if (fixedLoai === "DEFENSE") {
      reviewers
        .filter(Boolean)
        .forEach((r) =>
          lecturers.push({ giangVienId: (r as any).id, role: ROLE.PHAN_BIEN })
        );
    }

    const body = {
      tenHoiDong,
      thoiGianBatDau: ngayBatDau, // 'yyyy-MM-dd' từ <input type="date">
      thoiGianKetThuc: ngayKetThuc, // 'yyyy-MM-dd'
      loaiHoiDong: fixedLoai, // 'DEFENSE' | 'PEER_REVIEW'
      dotBaoVeId: dotIdCreate,
      lecturers,
    };

    try {
      const res = await createHoiDong(body as any);
      if ((res as any).code === 1000) {
        toast.success("Tạo hội đồng thành công");
        setOpenCreate(false);
        // reset form...
        setTenHoiDong("");
        setNgayBatDau("");
        setNgayKetThuc("");
        setChuTich(null);
        setThuKy(null);
        setReviewers([null]);
        setNamBatDau("");
        setHocKi("");
        setDotThu("");
        setDotIdCreate(null);
        setPage(0);
        loadData();
      } else {
        toast.error("Tạo hội đồng thất bại");
      }
    } catch (err: any) {
      // log message chi tiết nếu BE trả về
      const msg = err?.response?.data?.message || "Tạo hội đồng thất bại";
      toast.error(msg);
      console.error(
        "create body sent:",
        body,
        "server error:",
        err?.response?.data
      );
    }
  };

  /* ====== DETAIL ====== */
  useEffect(() => {
    (async () => {
      if (!detailId) return;
      try {
        const d = await getHoiDongDetail(detailId);
        setDetail(d);
      } catch {
        toast.error("Không tải được chi tiết hội đồng");
        setDetailId(null);
      }
    })();
  }, [detailId]);

  /* ====== IMPORT ====== */
  const onPickFile = (hoiDongId: number) => {
    setImportHoiDongId(hoiDongId);
    fileInputRef.current?.click();
  };

  const onFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0];
    if (!f || !importHoiDongId) return;

    try {
      const result: ImportResult = await importSinhVienToHoiDong(
        importHoiDongId,
        f
      );

      toast.info(
        `Import xong: ${result.successCount} thành công, ${result.failureCount} thất bại`
      );

      if (result.logFileUrl) {
        setTimeout(() => {
          const confirmMsg =
            result.failureCount > 0
              ? `Có ${result.failureCount} bản ghi lỗi. Bạn có muốn tải file kết quả (.xlsx) về không?`
              : `Import thành công. Bạn có muốn tải file kết quả (.xlsx) về không?`;

          if (window.confirm(confirmMsg)) {
            downloadByUrl(result.logFileUrl!, "ket_qua_import.xlsx");
          }
        }, 300);
      }

      loadData();
    } catch (err: any) {
      console.error(err);
      const status = err?.response?.status || err?.status;
      if (status === 415)
        toast.error("Import thất bại: Sai Content-Type (multipart/form-data).");
      else toast.error("Import thất bại");
    } finally {
      e.target.value = "";
      setImportHoiDongId(null);
    }
  };

  return (
    <div>
      {/* ===== breadcrumb ===== */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="#">
              {title}
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-6">{title}</h1>

      {/* ===== toolbar ===== */}
      <div className="flex justify-between items-center mb-4">
        <div className="flex items-center gap-2">
          {userRole === "TRO_LY_KHOA" && (
            <Button
              onClick={() => setOpenCreate(true)}
              className="h-10 px-4 font-semibold rounded-md bg-[#457B9D] hover:bg-[#3A6E90] text-white shadow-sm"
            >
              Tạo hội đồng
            </Button>
          )}
        </div>

        <form
          className="flex items-center gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            loadData();
          }}
        >
          {/* chọn đợt */}
          <select
            className="h-10 min-w-[220px] rounded-md border border-gray-300 px-3 text-sm bg-white"
            value={dotId ?? ""}
            onChange={(e) => {
              setPage(0);
              setDotId(Number(e.target.value) || null);
            }}
          >
            {dotOptions.map((o) => (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            ))}
          </select>

          {/* tìm tên hội đồng */}
          <Input
            type="text"
            placeholder="Tìm theo tên hội đồng..."
            className="w-[260px] border-gray-300 h-10"
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
          <Button
            type="submit"
            className="h-10 border-gray-300"
            variant="outline"
          >
            <Search />
          </Button>
        </form>
      </div>

      {/* ===== table ===== */}
      <Table className="mt-2 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">
              STT
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Tên hội đồng
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Bắt đầu
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Kết thúc
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Hành động
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {!loading && rows.length === 0 && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={6}
              >
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}

          {rows.map((r, i) => (
            <TableRow key={r.id}>
              <TableCell className="text-center border border-gray-300">
                {page * size + i + 1}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {r.tenHoiDong}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {fmt(r.thoiGianBatDau)}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {fmt(r.thoiGianKetThuc)}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                <div className="flex gap-3 justify-center">
                  <button
                    type="button"
                    title="Xem chi tiết"
                    className="w-5 h-5 text-blue-500 hover:scale-110 transition-transform"
                    onClick={() => setDetailId(r.id)}
                  >
                    <Eye className="w-5 h-5" />
                  </button>

                  {userRole === "TRO_LY_KHOA" && (
                    <button
                      type="button"
                      title="Import sinh viên"
                      className="w-5 h-5 text-emerald-600 hover:scale-110 transition-transform"
                      onClick={() => onPickFile(r.id)}
                    >
                      <Upload className="w-5 h-5" />
                    </button>
                  )}
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* ===== pagination ===== */}
      <div className="flex justify-end mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e) => {
              e.preventDefault();
              if (page > 0) setPage(page - 1);
            }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
              page === 0
                ? "pointer-events-none opacity-50"
                : "hover:bg-gray-200"
            }`}
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={(e) => {
                e.preventDefault();
                setPage(i);
              }}
              className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 ${
                page === i
                  ? "bg-[#2F80ED] text-white font-semibold"
                  : "bg-gray-100 hover:bg-gray-200"
              }`}
            >
              {i + 1}
            </button>
          ))}

          <button
            onClick={(e) => {
              e.preventDefault();
              if (page + 1 < totalPages) setPage(page + 1);
            }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
              page + 1 >= totalPages
                ? "pointer-events-none opacity-50"
                : "hover:bg-gray-200"
            }`}
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* input file ẩn dùng cho import */}
      <input
        ref={fileInputRef}
        type="file"
        accept=".xlsx,.xls"
        className="hidden"
        onChange={onFileChange}
      />

      {/* ===== DIALOG TẠO ===== */}
      <Dialog open={openCreate} onOpenChange={setOpenCreate}>
        <DialogContent className="w-[96vw] max-w-[900px] bg-white border border-gray-300 p-0">
          {/* Header */}
          <div className="px-6 pt-5 pb-3 border-b">
            <DialogHeader>
              <DialogTitle className="text-[22px] font-bold">
                Tạo hội đồng
              </DialogTitle>
            </DialogHeader>
          </div>

          {/* Body */}
          <div className="px-6 pb-6 pt-4 text-gray-800 max-h-[70vh] overflow-y-auto">
            {loadingGV && (
              <div className="text-sm text-gray-500 mb-3">
                Đang tải danh sách giảng viên…
              </div>
            )}

            {/* Hàng 1: Tên + Loại */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium">Tên hội đồng</label>
                <Input
                  value={tenHoiDong}
                  onChange={(e) => setTenHoiDong(e.target.value)}
                  className="mt-1"
                />
              </div>

              <div>
                <label className="text-sm font-medium">Loại</label>
                <Input
                  value={toVnLoai(fixedLoai)}
                  disabled
                  readOnly
                  className="mt-1 font-semibold bg-gray-200 text-gray-900 disabled:text-gray-900 disabled:opacity-100 border border-gray-300 cursor-not-allowed"
                />
              </div>
            </div>

            {/* Hàng 2: thông tin đợt → suy ra dotBaoVeId */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-4">
              <div>
                <label className="text-sm font-medium">Năm bắt đầu</label>
                <Input
                  type="number"
                  value={namBatDau}
                  onChange={(e) => setNamBatDau(e.target.value)}
                  className="mt-1"
                  placeholder="VD: 2025"
                />
              </div>

              <div>
                <label className="text-sm font-medium">Học kỳ</label>
                <Input
                  type="number"
                  value={hocKi}
                  onChange={(e) => setHocKi(e.target.value)}
                  className="mt-1"
                  placeholder="1 hoặc 2"
                />
              </div>

              <div>
                <label className="text-sm font-medium">Đợt thứ</label>
                <Input
                  type="number"
                  value={dotThu}
                  onChange={(e) => setDotThu(e.target.value)}
                  className="mt-1"
                  placeholder="1, 2, 3…"
                />
              </div>

              {/* Trạng thái đợt – xuống dòng, chiếm full row */}
              <div className="md:col-span-4">
                <label className="text-sm font-medium">Trạng thái đợt</label>
                <div className="mt-1 rounded-md border bg-gray-50 p-3">
                  {findingDot ? (
                    <div className="flex items-start gap-2">
                      <Loader2 className="w-4 h-4 animate-spin text-gray-500 mt-0.5" />
                      <span className="text-sm text-gray-600">
                        Đang xác định…
                      </span>
                    </div>
                  ) : Number(namBatDau) && Number(hocKi) && Number(dotThu) ? (
                    dotIdCreate ? (
                      <div className="flex items-start gap-2">
                        <CheckCircle className="w-5 h-5 text-emerald-600 mt-0.5" />
                        <div className="text-sm text-gray-900">
                          <div className="font-medium">Tìm thấy đợt</div>
                          <div>
                            Đợt {dotThu} · HK {hocKi} · Năm {namBatDau}
                          </div>
                          {/* Không hiển thị ID ở đây nữa */}
                        </div>
                      </div>
                    ) : (
                      <div className="flex items-start gap-2">
                        <XCircle className="w-5 h-5 text-red-500 mt-0.5" />
                        <div className="text-sm text-red-600">
                          <div className="font-medium">
                            Không tìm thấy đợt phù hợp
                          </div>
                          <div className="text-gray-600">
                            Kiểm tra lại Năm/Học kỳ/Đợt thứ.
                          </div>
                        </div>
                      </div>
                    )
                  ) : (
                    <span className="text-sm text-gray-500">
                      Nhập đủ 3 ô để xác định đợt
                    </span>
                  )}
                </div>
              </div>
            </div>

            {/* Hàng 3: Bắt đầu + Kết thúc */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div>
                <label className="text-sm font-medium">Bắt đầu</label>
                <Input
                  type="date"
                  value={ngayBatDau}
                  onChange={(e) => setNgayBatDau(e.target.value)}
                  className="mt-1"
                />
              </div>
              <div>
                <label className="text-sm font-medium">Kết thúc</label>
                <Input
                  type="date"
                  value={ngayKetThuc}
                  onChange={(e) => setNgayKetThuc(e.target.value)}
                  className="mt-1"
                />
              </div>
            </div>

            {/* Dọc: Chủ tịch */}
            <div className="mt-4">
              <label className="text-sm font-medium">
                Chủ tịch hội đồng <span className="text-red-500">*</span>
              </label>
              <div className="mt-1">
                <SuggestGVInput
                  value={chuTich}
                  onSelect={setChuTich}
                  source={allGV}
                  placeholder="Nhập tên/mã GV (Chủ tịch)…"
                />
              </div>
            </div>

            {/* Dọc: Thư ký (chỉ Bảo vệ) */}
            {fixedLoai === "DEFENSE" && (
              <div className="mt-4">
                <label className="text-sm font-medium">
                  Thư ký hội đồng <span className="text-red-500">*</span>
                </label>
                <div className="mt-1">
                  <SuggestGVInput
                    value={thuKy}
                    onSelect={setThuKy}
                    source={allGV}
                    placeholder="Nhập tên/mã GV (Thư ký)…"
                  />
                </div>
              </div>
            )}

            {/* Dọc: Giảng viên phản biện (chỉ Bảo vệ) */}
            {fixedLoai === "DEFENSE" && (
              <div className="mt-4">
                <div className="flex items-center justify-between">
                  <label className="text-sm font-medium">
                    Giảng viên phản biện
                  </label>

                  <button
                    type="button"
                    onClick={addReviewer}
                    className="h-8 px-3 rounded-md bg-[#457B9D] text-white hover:bg-[#3A6E90] inline-flex items-center"
                  >
                    <Plus className="w-4 h-4 mr-1" />
                    Thêm giảng viên phản biện
                  </button>
                </div>

                <div className="mt-2 space-y-3">
                  {reviewers.map((val, idx) => (
                    <div key={idx} className="flex items-center gap-2">
                      <div className="flex-1">
                        <SuggestGVInput
                          value={val}
                          onSelect={(v) => setReviewerAt(idx, v)}
                          source={allGV}
                          placeholder="Nhập tên/mã GV phản biện…"
                        />
                      </div>
                      {reviewers.length > 1 && (
                        <Button
                          type="button"
                          variant="outline"
                          onClick={() => removeReviewer(idx)}
                          className="shrink-0"
                          title="Xóa"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Footer buttons */}
            <div className="flex justify-end gap-3 pt-6">
              <button
                type="button"
                onClick={() => setOpenCreate(false)}
                className="px-6 h-10 rounded-lg bg-gray-300 text-white hover:bg-gray-400"
              >
                Hủy
              </button>
              <button
                type="button"
                onClick={doCreate}
                disabled={creating}
                className={`px-6 h-10 rounded-lg text-white ${
                  creating ? "opacity-50 cursor-not-allowed" : ""
                }`}
                style={{ backgroundColor: "#457B9D" }}
              >
                Tạo
              </button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      {/* ===== DIALOG CHI TIẾT ===== */}
      <Dialog
        open={detailId !== null}
        onOpenChange={() => {
          setDetailId(null);
          setDetail(null);
        }}
      >
        <DialogContent className="!w-[78vw] sm:!max-w-[820px] lg:!max-w-[920px] xl:!max-w-[980px] bg-white border border-gray-300 p-0">
          <div className="px-6 pt-5 pb-3 border-b">
            <DialogHeader>
              <DialogTitle className="text-[22px] font-bold">
                Chi tiết hội đồng
              </DialogTitle>
            </DialogHeader>
          </div>

          <div className="px-6 pb-6 pt-4 max-h-[70vh] overflow-y-auto text-gray-800">
            {!detail && <div className="text-gray-500">Đang tải...</div>}

            {detail && (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-[15px]">
                  <p>
                    <b>Tên hội đồng:</b> {detail.tenHoiDong}
                  </p>
                  <p>
                    <b>Loại:</b> {toVnLoai(detail.loaiHoiDong as any)}
                  </p>
                  <p>
                    <b>Bắt đầu:</b> {fmt(detail.thoiGianBatDau)}
                  </p>
                  <p>
                    <b>Kết thúc:</b> {fmt(detail.thoiGianKetThuc)}
                  </p>
                </div>

                <div className="mt-2">
                  <div className="flex flex-col gap-2 text-[15px]">
                    <p>
                      <b>Chủ tịch hội đồng:</b> {detail.chuTich ?? "-"}
                    </p>
                    <p>
                      <b>Thư ký hội đồng:</b> {detail.thuKy ?? "-"}
                    </p>

                    <div>
                      <p className="font-medium">
                        <b>Giảng viên phản biện</b>
                      </p>
                      <ul className="list-disc ml-5">
                        {(detail.giangVienPhanBien ?? []).length === 0 ? (
                          <li className="list-none text-gray-500">-</li>
                        ) : (
                          (detail.giangVienPhanBien ?? []).map((gv, i) => (
                            <li key={i}>{gv}</li>
                          ))
                        )}
                      </ul>
                    </div>
                  </div>
                </div>

                <div>
                  <p className="font-semibold mb-2">
                    Sinh viên{" "}
                    <span className="ml-1 text-sm text-gray-500">
                      ({detail.sinhVienList?.length ?? 0})
                    </span>
                  </p>

                  <div className="rounded-lg border overflow-hidden">
                    <table className="w-full text-sm table-fixed">
                      <colgroup>
                        <col style={{ width: 56 }} />
                        <col style={{ width: 120 }} />
                        <col style={{ width: 220 }} />
                        <col style={{ width: 110 }} />
                        <col />
                        <col style={{ width: 220 }} />
                      </colgroup>

                      <thead className="sticky top-0 z-10">
                        <tr className="bg-[#457B9D] text-white">
                          <th className="p-3 font-semibold text-center first:rounded-tl-lg">
                            STT
                          </th>
                          <th className="p-3 font-semibold">Mã SV</th>
                          <th className="p-3 font-semibold">Họ tên</th>
                          <th className="p-3 font-semibold">Lớp</th>
                          <th className="p-3 font-semibold">Tên đề tài</th>
                          <th className="p-3 font-semibold last:rounded-tr-lg">
                            GVHD
                          </th>
                        </tr>
                      </thead>

                      <tbody>
                        {(detail.sinhVienList ?? []).map((sv, i) => (
                          <tr key={i} className="border-t hover:bg-gray-50">
                            <td className="p-3 text-center align-top">
                              {i + 1}
                            </td>
                            <td className="p-3 align-top">{sv.maSV}</td>
                            <td className="p-3 align-top">{sv.hoTen}</td>
                            <td className="p-3 align-top">{sv.lop}</td>
                            <td className="p-3 align-top whitespace-normal break-words">
                              {sv.tenDeTai}
                            </td>
                            <td className="p-3 align-top">{sv.gvhd}</td>
                          </tr>
                        ))}

                        {(!detail.sinhVienList ||
                          detail.sinhVienList.length === 0) && (
                          <tr>
                            <td className="p-3 text-gray-500" colSpan={6}>
                              Chưa có sinh viên
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>

                <div className="flex justify-end">
                  <Button
                    onClick={() => {
                      setDetailId(null);
                      setDetail(null);
                    }}
                    className="min-w-[96px]"
                    variant="outline"
                  >
                    Đóng
                  </Button>
                </div>
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

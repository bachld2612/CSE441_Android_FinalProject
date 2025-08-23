import { useEffect, useMemo, useRef, useState } from "react";
import {
  Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList,
  BreadcrumbPage, BreadcrumbSeparator
} from "@/components/ui/breadcrumb";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { getThongBaoPage, type ThongBaoResponse } from "@/services/thong-bao.service";
import { downloadFile } from "@/lib/downloadFile";
import { toast } from "react-toastify";

/* ========== Cấu hình hiển thị ========== */
const PAGE_SIZE = 10;

/* ========== Helpers ========== */
function formatTimeLabel(createdAt?: string) {
  if (!createdAt) return "";
  const pad = (n: number) => n.toString().padStart(2, "0");
  const toDDMMYYYY = (d: Date) => `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  const isDateOnly = /^\d{4}-\d{2}-\d{2}$/.test(createdAt);
  if (isDateOnly) {
    const dt = new Date(`${createdAt}T00:00:00`);
    const now = new Date();
    const startToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const startThat  = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate());
    const diffDays = Math.floor((startToday.getTime() - startThat.getTime()) / 86400000);
    if (diffDays === 0) return "Hôm nay";
    if (diffDays === 1) return "Hôm qua";
    return toDDMMYYYY(dt);
  }
  const dt = new Date(createdAt);
  if (isNaN(dt.getTime())) return createdAt;
  const now = new Date();
  const diffMs = now.getTime() - dt.getTime();
  const diffMin = Math.floor(diffMs / 60000);
  const diffHour = Math.floor(diffMin / 60);
  if (diffMin < 1) return "Vừa xong";
  if (diffMin < 60) return `${diffMin} phút trước`;
  if (diffHour < 24) return `${diffHour} giờ trước`;
  return toDDMMYYYY(dt);
}

function slugify(s: string) {
  return (s || "thong-bao")
    .toLowerCase()
    .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)+/g, "");
}

// Lấy tên file từ URL nếu có (cloudinary/raw file v.v.)
function fileNameFromUrl(url: string): string {
  try {
    const u = new URL(url);
    const last = u.pathname.split("/").filter(Boolean).pop() || "file.pdf";
    // Nếu không có đuôi, mặc định pdf
    return last.includes(".") ? last : `${last}.pdf`;
  } catch {
    const segs = url.split("?")[0].split("/").filter(Boolean);
    const last = segs.pop() || "file.pdf";
    return last.includes(".") ? last : `${last}.pdf`;
  }
}

/* ========== Lưu đã đọc localStorage ========== */
const STORAGE_KEY = "readThongBaoIds";
function loadReadSet(): Set<number> {
  try { return new Set<number>(JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]")); }
  catch { return new Set<number>(); }
}
function saveReadSet(set: Set<number>) {
  try { localStorage.setItem(STORAGE_KEY, JSON.stringify([...set])); } catch {}
}

// Parse createdAt (LocalDate "yyyy-MM-dd" hoặc ISO)
function parseCreatedAt(d?: string): number {
  if (!d) return 0;
  if (/^\d{4}-\d{2}-\d{2}$/.test(d)) return new Date(`${d}T00:00:00`).getTime();
  const t = new Date(d).getTime();
  return isNaN(t) ? 0 : t;
}

export default function ThongBaoLatestPage() {
  const [items, setItems] = useState<ThongBaoResponse[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [readIds, setReadIds] = useState<Set<number>>(() => loadReadSet());
  const [tab, setTab] = useState<"all" | "unread">("all");
  const firstLoadRef = useRef(true);

  const [q, setQ] = useState("");

  const markRead = (id: number) => {
    setReadIds(prev => {
      if (prev.has(id)) return prev;
      const next = new Set(prev);
      next.add(id);
      saveReadSet(next);
      return next;
    });
  };

  const fetchPage = async (p: number) => {
    setLoading(true);
    try {
      const res = await getThongBaoPage({ page: p, size: PAGE_SIZE, sort: "updatedAt,DESC" });
      if (!res.result) {
        toast.error(res.message || "Không tải được danh sách thông báo");
        return;
      }
      const pageData = res.result.content || [];

      // 🔒 Lưới an toàn: sort FE theo createdAt desc (nếu BE không sort)
      pageData.sort((a, b) => {
        const diff = parseCreatedAt(b.createdAt) - parseCreatedAt(a.createdAt);
        return diff !== 0 ? diff : (b.id ?? 0) - (a.id ?? 0);
      });

      setItems(prev =>
        p === 0 ? pageData : [...prev, ...pageData]
      );
      setTotalPages(res.result.totalPages || 0);
    } catch {
      toast.error("Không tải được danh sách thông báo");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPage(0); }, []);

  // Lọc tab + tìm nhanh trên FE
  const filtered = useMemo(() => {
    let arr = items;
    if (tab === "unread") arr = arr.filter(x => !readIds.has(x.id));
    if (q.trim()) {
      const t = q.trim().toLowerCase();
      arr = arr.filter(x =>
        (x.tieuDe || "").toLowerCase().includes(t) ||
        (x.noiDung || "").toLowerCase().includes(t)
      );
    }
    // đảm bảo luôn mới → cũ sau khi filter
    return [...arr].sort((a, b) => {
      const diff = parseCreatedAt(b.createdAt) - parseCreatedAt(a.createdAt);
      return diff !== 0 ? diff : (b.id ?? 0) - (a.id ?? 0);
    });
  }, [items, tab, readIds, q]);

  const canLoadMore = page + 1 < totalPages;

  const handleLoadMore = () => {
    if (!canLoadMore || loading) return;
    const next = page + 1;
    setPage(next);
    fetchPage(next);
  };

  const onClickItem = (tb: ThongBaoResponse) => {
    markRead(tb.id);
    if (!tb.fileUrl) return; // không có file thì chỉ đánh dấu đã đọc
    const name = fileNameFromUrl(tb.fileUrl || "") || `${slugify(tb.tieuDe || "thong-bao")}.pdf`;
    downloadFile(tb.fileUrl, name);
  };

  const onDownloadClick = (e: React.MouseEvent, tb: ThongBaoResponse) => {
    e.stopPropagation();
    if (!tb.fileUrl) return;
    const name = fileNameFromUrl(tb.fileUrl || "") || `${slugify(tb.tieuDe || "thong-bao")}.pdf`;
    downloadFile(tb.fileUrl, name);
    markRead(tb.id);
  };

  const onRefresh = async () => {
    setPage(0);
    await fetchPage(0);
  };

  return (
    <div className="pb-10">
      {/* Breadcrumb */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="#">
              Thông báo mới nhất
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-6">
        Danh sách thông báo mới nhất
      </h1>

      {/* Toolbar */}
      <div className="flex flex-wrap items-center gap-3 justify-between mb-4">
        <div className="flex gap-4">
          <button
            className={`pb-2 text-sm ${tab === "all" ? "font-semibold border-b-2 border-black" : "text-gray-600"}`}
            onClick={() => setTab("all")}
          >
            Tất cả
          </button>
          <button
            className={`pb-2 text-sm ${tab === "unread" ? "font-semibold border-b-2 border-black" : "text-gray-600"}`}
            onClick={() => setTab("unread")}
          >
            Chưa đọc
          </button>
        </div>

        <div className="flex items-center gap-2">
          <Input
            placeholder="Tìm nhanh theo tiêu đề/nội dung..."
            value={q}
            onChange={(e) => setQ(e.target.value)}
            className="w-[260px]"
          />
          {/* Làm mới: #BFBFBF */}
          <Button
            type="button"
            onClick={onRefresh}
            className="bg-[#BFBFBF] hover:bg-[#a6a6a6] text-black border-0"
            disabled={loading}
          >
            {loading ? "Đang tải..." : "Làm mới"}
          </Button>
        </div>
      </div>

      {/* List */}
      <div className="grid grid-cols-1 gap-3">
        {filtered.length === 0 && !loading && (
          <div className="text-center text-gray-600 py-8 border rounded-md bg-white">
            Không có thông báo
          </div>
        )}

        {filtered.map((tb) => {
          const isRead = readIds.has(tb.id);
          const hasFile = !!tb.fileUrl;
          const fname = hasFile ? fileNameFromUrl(tb.fileUrl!) : "";
          return (
            <div
              key={tb.id}
              className="border rounded-md bg-white p-4 hover:bg-gray-50 cursor-pointer"
              onClick={() => onClickItem(tb)}
              title={hasFile ? "Bấm để tải PDF & đánh dấu đã đọc" : "Bấm để đánh dấu đã đọc"}
            >
              <div className="flex gap-3">
                <div className="w-10 h-10 rounded-full bg-gray-200 flex-shrink-0" />
                <div className="min-w-0">
                  <div className={`text-[15px] font-semibold ${isRead ? "text-gray-700" : "text-gray-900"} line-clamp-1`}>
                    {tb.tieuDe}
                  </div>

                  <div className={`text-[14px] ${isRead ? "text-gray-500" : "text-gray-700"} line-clamp-2`}>
                    {tb.noiDung}
                  </div>

                  {/* Hiển thị file nếu có */}
                  {hasFile && (
                    <div className="mt-1">
                      <button
                        className="text-[13px] underline text-blue-600 hover:text-blue-700"
                        onClick={(e) => onDownloadClick(e, tb)}
                        title="Tải tệp PDF"
                      >
                        PDF: {fname}
                      </button>
                    </div>
                  )}

                  <div className="text-[12px] text-gray-500 mt-1">
                    {formatTimeLabel(tb.createdAt)}
                  </div>
                </div>

                {!isRead && <div className="ml-auto mt-2 w-2 h-2 bg-blue-500 rounded-full" />}
              </div>
            </div>
          );
        })}
      </div>

      {/* Phân trang “Tải thêm” */}
      <div className="flex justify-center mt-6">
        {canLoadMore && (
          <Button
            onClick={handleLoadMore}
            disabled={loading}
            variant="outline"
            className="min-w-[160px]"
          >
            {loading ? "Đang tải..." : "Tải thêm"}
          </Button>
        )}
      </div>
    </div>
  );
}

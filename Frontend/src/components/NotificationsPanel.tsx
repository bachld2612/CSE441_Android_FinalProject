// src/components/NotificationsPanel.tsx
import { useEffect, useMemo, useRef, useState } from "react";
import { createPortal } from "react-dom";
import { Bell, X } from "lucide-react";
import { Link, useLocation } from "react-router-dom";
import {
  getThongBaoPage,
  type ThongBaoResponse,
} from "@/services/thongBao.service";
import { downloadFile } from "@/lib/downloadFile";
import { toast } from "react-toastify";
import { Dialog, DialogContent } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

/* ====== Cấu hình hiển thị ====== */
const ALL_MIN = 3;
const ALL_MAX = 20;
const UNREAD_MIN = 3;
const UNREAD_MAX = 20;

/* ====== Helpers ====== */
function formatTimeLabel(createdAt?: string) {
  if (!createdAt) return "";
  const pad = (n: number) => n.toString().padStart(2, "0");
  const toDDMMYYYY = (d: Date) =>
    `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  const isDateOnly = /^\d{4}-\d{2}-\d{2}$/.test(createdAt);
  if (isDateOnly) {
    const dt = new Date(`${createdAt}T00:00:00`);
    const now = new Date();
    const startToday = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate()
    );
    const startThat = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate());
    const diffDays = Math.floor(
      (startToday.getTime() - startThat.getTime()) / 86400000
    );
    if (diffDays === 0) return "Hôm nay";
    if (diffDays === 1) return "Hôm qua";
    return toDDMMYYYY(dt);
  }
  const dt = new Date(createdAt);
  if (isNaN(dt.getTime())) return createdAt;
  const now = new Date();
  const diff = now.getTime() - dt.getTime();
  const mins = Math.floor(diff / 60000);
  const hours = Math.floor(mins / 60);
  if (mins < 1) return "Vừa xong";
  if (mins < 60) return `${mins} phút`;
  if (hours < 24) return `${hours} giờ`;
  return toDDMMYYYY(dt);
}
function slugify(s: string) {
  return (s || "thong-bao")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)+/g, "");
}
function fileNameFromUrl(url: string): string {
  try {
    const u = new URL(url);
    const last = u.pathname.split("/").filter(Boolean).pop() || "file.pdf";
    return last.includes(".") ? last : `${last}.pdf`;
  } catch {
    const segs = url.split("?")[0].split("/").filter(Boolean);
    const last = segs.pop() || "file.pdf";
    return last.includes(".") ? last : `${last}.pdf`;
  }
}

/* ====== Lưu đã đọc ở localStorage ====== */
const STORAGE_KEY = "readThongBaoIds";
function loadReadSet(): Set<number> {
  try {
    return new Set<number>(
      JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]")
    );
  } catch {
    return new Set<number>();
  }
}
function saveReadSet(set: Set<number>) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify([...set]));
  } catch {}
}

export default function NotificationsPanel() {
  const [open, setOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<"all" | "unread">("all");
  const [showAll, setShowAll] = useState(false);
  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState<ThongBaoResponse[]>([]);
  const [total, setTotal] = useState(0);

  const [readIds, setReadIds] = useState<Set<number>>(() => loadReadSet());
  const [manualUnreadBump, setManualUnreadBump] = useState(0);

  const bellRef = useRef<HTMLButtonElement | null>(null);
  const panelRef = useRef<HTMLDivElement | null>(null);

  // Dialog chi tiết
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selected, setSelected] = useState<ThongBaoResponse | null>(null);

  /* ====== Dữ liệu theo tab ====== */
  const unreadList = useMemo(
    () => items.filter((tb) => !readIds.has(tb.id)),
    [items, readIds]
  );
  const allVisible = useMemo(
    () => items.slice(0, showAll ? ALL_MAX : ALL_MIN),
    [items, showAll]
  );
  const unreadVisible = useMemo(
    () => unreadList.slice(0, showAll ? UNREAD_MAX : UNREAD_MIN),
    [unreadList, showAll]
  );

  /* ====== Prefetch tổng số để badge ====== */
  useEffect(() => {
    (async () => {
      try {
        const res = await getThongBaoPage({
          page: 0,
          size: 1,
          sort: "updatedAt,DESC",
        });
        if (res.result) setTotal(res.result.totalElements || 0);
      } catch {}
    })();
  }, []);

  /* ====== +1 khi FE tạo mới ====== */
  useEffect(() => {
    const handler = () => setManualUnreadBump((c) => c + 1);
    window.addEventListener("thongbao:new", handler);
    return () => window.removeEventListener("thongbao:new", handler);
  }, []);

  /* ====== Load list ====== */
  const load = async () => {
    try {
      setLoading(true);
      const res = await getThongBaoPage({
        page: 0,
        size: 50,
        sort: "updatedAt,DESC",
      });
      if (!res.result) {
        toast.error(res.message || "Không tải được thông báo");
        return;
      }
      setItems(res.result.content || []);
      setTotal(res.result.totalElements || 0);
      setManualUnreadBump(0);
    } catch {
      toast.error("Không tải được thông báo");
    } finally {
      setLoading(false);
    }
  };
  const onToggle = async () => {
    const next = !open;
    setOpen(next);
    if (next && items.length === 0) await load();
  };

  /* ====== Đóng panel khi click ngoài / ESC ====== */
  useEffect(() => {
    if (!open) return;
    const onDown = (e: MouseEvent) => {
      const t = e.target as Node;
      if (
        panelRef.current &&
        !panelRef.current.contains(t) &&
        bellRef.current &&
        !bellRef.current.contains(t)
      ) {
        setOpen(false);
      }
    };
    const onEsc = (e: KeyboardEvent) => e.key === "Escape" && setOpen(false);
    document.addEventListener("mousedown", onDown);
    document.addEventListener("keydown", onEsc);
    return () => {
      document.removeEventListener("mousedown", onDown);
      document.removeEventListener("keydown", onEsc);
    };
  }, [open]);

  /* ====== Đổi route thì đóng panel ====== */
  const location = useLocation();
  useEffect(() => {
    setOpen(false);
  }, [location.pathname]);

  /* ====== Badge: số chưa đọc ====== */
  const unreadCountLoaded = items.length
    ? unreadList.length
    : Math.max(total - readIds.size, 0);
  const badge = items.length
    ? Math.min(unreadCountLoaded + manualUnreadBump, 99)
    : Math.min(Math.max(unreadCountLoaded + manualUnreadBump, 0), 99);

  /* ====== Item actions ====== */
  const markRead = (id: number) => {
    setReadIds((prev) => {
      if (prev.has(id)) return prev;
      const next = new Set(prev);
      next.add(id);
      saveReadSet(next);
      return next;
    });
  };
  const openDetail = (tb: ThongBaoResponse) => {
    markRead(tb.id);
    setSelected(tb);
    setDialogOpen(true);
    setOpen(false); // đóng panel khi mở dialog
  };

  const switchTab = (tab: "all" | "unread") => {
    setActiveTab(tab);
    setShowAll(false);
  };
  const listToRender = activeTab === "all" ? allVisible : unreadVisible;
  const canShowMore =
    activeTab === "all"
      ? items.length > ALL_MIN
      : unreadList.length > UNREAD_MIN;
  const footerLabel =
    activeTab === "all" ? "Xem thông báo trước đó" : "Xem thông báo chưa đọc";

  return (
    <div className="relative">
      {/* Nút chuông */}
      <button
        ref={bellRef}
        onClick={onToggle}
        className="relative"
        aria-label="Thông báo"
      >
        <Bell className="w-5 h-5 text-gray-800" />
        {badge > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] rounded-full min-w-4 h-4 px-[2px] flex items-center justify-center">
            {badge}
          </span>
        )}
      </button>

      {/* PANEL overlay */}
      {open &&
        createPortal(
          <div className="fixed inset-0 z-[9999]">
            <div className="absolute inset-0" onClick={() => setOpen(false)} />
            <div
              ref={panelRef}
              className={`absolute top-16 right-4 w-[380px] bg-white rounded-xl shadow-2xl border border-gray-200 ${
                showAll ? "max-h-[80vh]" : ""
              } z-[10000] overflow-hidden`}
            >
              {/* Header */}
              <div className="flex items-center justify-between px-4 py-3">
                <div className="text-[15px] font-semibold">Thông báo</div>
                <button
                  onClick={() => setOpen(false)}
                  className="p-1 rounded hover:bg-gray-100"
                  aria-label="Đóng"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              {/* Tabs (giữ gạch chân đen như yêu cầu trước đó) */}
              <div className="px-4">
                <div className="flex gap-3 text-sm">
                  <button
                    className={`pb-2 ${
                      activeTab === "all"
                        ? "font-semibold border-b-2 border-black"
                        : "text-gray-600"
                    }`}
                    onClick={() => switchTab("all")}
                  >
                    Tất cả
                  </button>
                  <button
                    className={`pb-2 ${
                      activeTab === "unread"
                        ? "font-semibold border-b-2 border-black"
                        : "text-gray-600"
                    }`}
                    onClick={() => switchTab("unread")}
                  >
                    Chưa đọc ({unreadList.length})
                  </button>
                </div>
              </div>

              {/* Nội dung panel */}
              <div
                className={`${showAll ? "max-h-[65vh] overflow-y-auto" : ""}`}
              >
                {loading ? (
                  <div className="px-4 py-8 text-center text-sm text-gray-600">
                    Đang tải...
                  </div>
                ) : listToRender.length === 0 ? (
                  <div className="px-4 py-8 text-center text-sm text-gray-600">
                    {activeTab === "unread"
                      ? "Không có thông báo chưa đọc"
                      : "Không có thông báo"}
                  </div>
                ) : (
                  <ul className="mt-2">
                    {!showAll && (
                      <li className="px-4 py-2 text-[13px] text-gray-500">
                        {activeTab === "unread" ? "Chưa đọc" : "Mới"}
                      </li>
                    )}
                    {listToRender.map((tb) => {
                      const isRead = readIds.has(tb.id);
                      return (
                        <li
                          key={tb.id}
                          className="px-4 py-3 hover:bg-gray-50 cursor-pointer"
                          onClick={() => openDetail(tb)}
                          title="Bấm để xem chi tiết"
                        >
                          <div className="flex gap-3">
                            <div className="w-10 h-10 rounded-full bg-gray-200 flex-shrink-0" />
                            <div className="min-w-0">
                              <div
                                className={`text-[14px] font-medium ${
                                  isRead ? "text-gray-700" : "text-gray-900"
                                } line-clamp-1`}
                              >
                                {tb.tieuDe}
                              </div>
                              <div
                                className={`text-[13px] ${
                                  isRead ? "text-gray-500" : "text-gray-700"
                                } line-clamp-2`}
                              >
                                {tb.noiDung}
                              </div>
                              <div className="text-[12px] text-gray-500 mt-1">
                                {formatTimeLabel(tb.createdAt)}
                              </div>
                            </div>
                            {!isRead && (
                              <div className="ml-auto mt-2 w-2 h-2 bg-blue-500 rounded-full" />
                            )}
                          </div>
                        </li>
                      );
                    })}
                  </ul>
                )}
              </div>

              {/* Footer panel — viền top border-gray-200; "Xem tất cả" KHÔNG border */}
              <div className="sticky bottom-0 bg-white border-t border-gray-200 px-4 py-3">
                <div className="flex gap-2">
                  {showAll ? (
                    <button
                      className="flex-1 text-sm font-medium py-2 rounded-md hover:bg-gray-50"
                      onClick={() => setShowAll(false)}
                    >
                      Thu gọn
                    </button>
                  ) : (
                    canShowMore && (
                      <button
                        className="flex-1 text-sm font-medium py-2 rounded-md hover:bg-gray-50"
                        onClick={() => setShowAll(true)}
                      >
                        {footerLabel}
                      </button>
                    )
                  )}

                  <Link
                    to="/thong-bao/moi-nhat"
                    className={`${
                      showAll || canShowMore ? "flex-1" : "w-full"
                    } text-center text-sm font-medium py-2 rounded-md hover:bg-gray-50`}
                  >
                    Xem tất cả
                  </Link>
                </div>
              </div>
            </div>
          </div>,
          document.body
        )}

      {/* DIALOG – grid để phần thân cuộn dọc; viền header/footer = border-gray-200 */}
      <Dialog
        open={dialogOpen}
        onOpenChange={(v) => {
          setDialogOpen(v);
          if (!v) setSelected(null);
        }}
      >
        <DialogContent className="w-[92vw] sm:w-[680px] max-w-2xl p-0 bg-white border border-gray-200 max-h-[80vh] grid grid-rows-[auto,minmax(0,1fr),auto]">
          {/* Header: border-200 */}
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-bold">
              {selected?.tieuDe || "Chi tiết thông báo"}
            </h2>
            <div className="text-xs text-gray-500 mt-1">
              {selected?.createdAt ? formatTimeLabel(selected.createdAt) : ""}
            </div>
          </div>

          {/* Body scroll */}
          <div className="px-6 py-4 overflow-y-auto overflow-x-hidden">
            <div className="whitespace-pre-wrap break-words [overflow-wrap:anywhere] text-[14px] leading-relaxed text-gray-800">
              {selected?.noiDung || ""}
            </div>

            {selected?.fileUrl ? (
              <div className="mt-4">
                <Button
                  className="bg-[#457B9D] hover:bg-[#3e6e8d] text-white border-0"
                  onClick={() => {
                    const name =
                      fileNameFromUrl(selected.fileUrl || "") ||
                      `${slugify(selected?.tieuDe || "thong-bao")}.pdf`;
                    downloadFile(selected.fileUrl!, name);
                  }}
                >
                  Tải PDF
                </Button>
              </div>
            ) : (
              <div className="mt-4 text-sm text-gray-500 italic">
                Không có tệp đính kèm.
              </div>
            )}
          </div>

          {/* Footer: border-200 */}
          <div className="px-6 py-3 border-t border-gray-200 flex justify-end gap-2">
            <Button
              className="bg-[#BFBFBF] hover:bg-[#a6a6a6] text-black border-0"
              onClick={() => setDialogOpen(false)}
            >
              Quay lại
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

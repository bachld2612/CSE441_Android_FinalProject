import { useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";
import { AxiosError } from "axios";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Pencil,
  ChevronLeft,
  ChevronRight,
  Plus,
  Calendar as CalendarIcon,
  ChevronsUpDown,
  Check,
} from "lucide-react";
import {
  getThoiGianThucHienPage,
  type Page as TGTHPage,
  type ThoiGianThucHien,
  createThoiGianThucHien,
  type ThoiGianThucHienCreateRequest,
  updateThoiGianThucHien,
} from "@/services/thoiGianThucHien.service";
import { format, parseISO } from "date-fns";

// Select (Loại công việc)
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";

// Combobox building blocks
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import {
  Command,
  CommandGroup,
  CommandItem,
  CommandList,
  CommandEmpty,
  CommandInput,
} from "@/components/ui/command";

// API đợt bảo vệ (typed)
import {
  getDotBaoVePage,
  type DotBaoVeResponse,
} from "@/services/dot-bao-ve.service";

/* =========================
   Helpers
========================= */
const labelCongViec = (cv: string) => {
  if (cv === "DANG_KY_DE_TAI") return "Đăng ký đồ án";
  if (cv === "NOP_DE_CUONG") return "Nộp đề cương";
  return cv;
};

const toVNDate = (iso: string) => {
  const [y, m, d] = iso.split("-");
  return y && m && d ? `${d}/${m}/${y}` : iso;
};

const toISO = (d?: Date) => (d ? format(d, "yyyy-MM-dd") : "");

const parseDateInput = (val?: string) => {
  if (!val) return undefined;
  const [y, m, d] = val.split("-").map(Number);
  if (!y || !m || !d) return undefined;
  return new Date(y, m - 1, d);
};

const normalize = (s?: string) =>
  (s || "").toLowerCase().replace(/\s+/g, " ").trim();

/* =========================
   DateInput
========================= */
type DateInputProps = {
  value?: Date;
  onChange: (d?: Date) => void;
  disabled?: boolean;
};

function DateInput({ value, onChange, disabled }: DateInputProps) {
  return (
    <div className="relative">
      <CalendarIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 pointer-events-none opacity-60" />
      <input
        type="date"
        value={toISO(value)}
        onChange={(e) => onChange(parseDateInput(e.target.value))}
        disabled={disabled}
        className="mt-1 w-full rounded border border-gray-300 bg-white pl-9 pr-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2F80ED] focus:border-[#2F80ED] disabled:bg-gray-100"
      />
    </div>
  );
}

/* =========================
   Combobox Đợt bảo vệ (searchable)
========================= */
type DotComboboxProps = {
  items: DotBaoVeResponse[];
  value: string; // id dạng string
  onChange: (id: string) => void;
  placeholder?: string;
  disabled?: boolean;
  loading?: boolean;
};

function dotLabel(d: DotBaoVeResponse) {
  return d.tenDotBaoVe || `HK${d.hocKi} năm học ${d.namBatDau}-${d.namKetThuc}`;
}

function DotBaoVeCombobox({
  items,
  value,
  onChange,
  placeholder = "-- Chọn đợt bảo vệ --",
  disabled,
  loading,
}: DotComboboxProps) {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");

  const selected = useMemo(
    () => items.find((i) => String(i.id) === value) || null,
    [items, value]
  );

  const filteredTop3 = useMemo(() => {
    const q = normalize(query);
    const base = q
      ? items.filter((d) => {
          const label = normalize(dotLabel(d));
          const hk = String(d.hocKi ?? "");
          const nam = `${d.namBatDau ?? ""}-${d.namKetThuc ?? ""}`;
          return label.includes(q) || hk.includes(q) || nam.includes(q);
        })
      : items;
    return base.slice(0, 3);
  }, [items, query]);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <button
          type="button"
          disabled={disabled}
          aria-expanded={open}
          className="mt-1 w-full h-10 bg-white border border-gray-300 rounded px-3 text-left
                     flex items-center justify-between focus:outline-none focus:ring-2
                     focus:ring-[#2F80ED] focus:border-[#2F80ED] disabled:bg-gray-100
                     disabled:cursor-not-allowed"
        >
          <span
            className={`${
              selected ? "text-gray-900" : "text-gray-500"
            } truncate`}
          >
            {selected
              ? dotLabel(selected)
              : loading
              ? "Đang tải..."
              : placeholder}
          </span>
          <ChevronsUpDown className="ml-2 h-4 w-4 text-gray-500" />
        </button>
      </PopoverTrigger>

      <PopoverContent
        align="start"
        sideOffset={4}
        className="p-0 bg-white border border-gray-300 max-h-36 overflow-y-auto w-[var(--radix-popover-trigger-width)]"
      >
        <Command shouldFilter={false}>
          <CommandInput
            placeholder="Tìm theo tên / HK / năm…"
            value={query}
            onValueChange={setQuery}
            className="border-b border-gray-200"
          />
          <CommandList>
            {loading ? (
              <div className="p-3 text-sm text-gray-500">Đang tải…</div>
            ) : (
              <>
                <CommandEmpty>Không tìm thấy</CommandEmpty>
                <CommandGroup>
                  {filteredTop3.map((d) => (
                    <CommandItem
                      key={d.id}
                      value={String(d.id)}
                      onSelect={() => {
                        onChange(String(d.id));
                        setOpen(false);
                        setQuery("");
                      }}
                      className="whitespace-normal break-words"
                    >
                      <Check
                        className={`mr-2 h-4 w-4 ${
                          value === String(d.id) ? "opacity-100" : "opacity-0"
                        }`}
                      />
                      {dotLabel(d)}
                    </CommandItem>
                  ))}
                </CommandGroup>
              </>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}

/* =========================
   Page
========================= */
export default function ThoiGianThucHienPage() {
  // ====== Bảng ======
  const [data, setData] = useState<ThoiGianThucHien[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // ====== Dialogs ======
  const [openCreate, setOpenCreate] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [editing, setEditing] = useState<ThoiGianThucHien | null>(null);

  // ====== Đợt bảo vệ ======
  const [dotBaoVeRows, setDotBaoVeRows] = useState<DotBaoVeResponse[]>([]);
  const [loadingDots, setLoadingDots] = useState(false);

  // TRẢ VỀ rows để có thể dùng ngay
  const fetchDots = async (): Promise<DotBaoVeResponse[] | undefined> => {
    setLoadingDots(true);
    try {
      const res = await getDotBaoVePage({
        page: 0,
        size: 50,
        sort: "updatedAt,DESC",
      });
      const rows = res.result?.content ?? [];
      setDotBaoVeRows(rows);
      return rows;
    } catch {
      toast.error("Không thể tải danh sách đợt bảo vệ");
    } finally {
      setLoadingDots(false);
    }
  };

  // ====== Form Create ======
  const [createCongViec, setCreateCongViec] =
    useState<string>("DANG_KY_DE_TAI");
  const [createStartDate, setCreateStartDate] = useState<Date | undefined>(
    undefined
  );
  const [createEndDate, setCreateEndDate] = useState<Date | undefined>(
    undefined
  );
  const [createDotId, setCreateDotId] = useState<string>("");
  const [submittingCreate, setSubmittingCreate] = useState(false);

  // ====== Form Edit ======
  const [editCongViec, setEditCongViec] = useState<string>("DANG_KY_DE_TAI");
  const [editStartDate, setEditStartDate] = useState<Date | undefined>(
    undefined
  );
  const [editEndDate, setEditEndDate] = useState<Date | undefined>(undefined);
  const [editDotId, setEditDotId] = useState<string>("");
  const [submittingEdit, setSubmittingEdit] = useState(false);

  const colCount = 5; // bỏ cột ID

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getThoiGianThucHienPage({ page, size });
      if (res.code === 1000 && res.result) {
        const p: TGTHPage<ThoiGianThucHien> = res.result;
        setData(p.content);
        setTotalPages(p.totalPages);
      } else {
        toast.error(res.message ?? "Không tải được dữ liệu", {
          autoClose: 3000,
        });
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(`Không tải được dữ liệu: ${err.message}`, {
          autoClose: 3000,
        });
      } else {
        toast.error("Không tải được dữ liệu", { autoClose: 3000 });
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  // ====== Open dialogs ======
  const openCreateDialog = async () => {
    setCreateCongViec("DANG_KY_DE_TAI");
    setCreateStartDate(undefined);
    setCreateEndDate(undefined);
    setCreateDotId("");
    await fetchDots();
    setOpenCreate(true);
  };

  const openEditDialog = async (row: ThoiGianThucHien) => {
    if (!Number.isFinite(Number((row as any).id))) {
      toast.error("Bản ghi thiếu ID nên không thể sửa.");
      return;
    }
    setEditing(row);
    setEditCongViec(row.congViec);
    setEditStartDate(parseISO(row.thoiGianBatDau));
    setEditEndDate(parseISO(row.thoiGianKetThuc));

    let idStr = (row as any).dotBaoVeId ? String((row as any).dotBaoVeId) : "";
    const rows = await fetchDots();
    if (!idStr && rows && row.tenDotBaoVe) {
      const found = rows.find(
        (d) =>
          normalize(d.tenDotBaoVe) === normalize(row.tenDotBaoVe) ||
          normalize(dotLabel(d)) === normalize(row.tenDotBaoVe)
      );
      if (found) idStr = String(found.id);
    }
    setEditDotId(idStr);
    setOpenEdit(true);
  };

  // Auto-deduce id sau khi dots về (phòng TH mở dialog trước khi dots xong)
  useEffect(() => {
    if (openEdit && editing && !editDotId && dotBaoVeRows.length) {
      const found = dotBaoVeRows.find(
        (d) =>
          normalize(d.tenDotBaoVe) === normalize(editing.tenDotBaoVe) ||
          normalize(dotLabel(d)) === normalize(editing.tenDotBaoVe)
      );
      if (found) setEditDotId(String(found.id));
    }
  }, [openEdit, editing, editDotId, dotBaoVeRows]);

  /* ====== Create ====== */
  const handleCreate = async () => {
    if (!createDotId) return toast.error("Vui lòng chọn đợt bảo vệ");
    if (!createStartDate || !createEndDate)
      return toast.error("Vui lòng chọn thời gian bắt đầu/kết thúc");
    if (createStartDate > createEndDate)
      return toast.error(
        "Thời gian bắt đầu phải nhỏ hơn hoặc bằng thời gian kết thúc"
      );

    const payload: ThoiGianThucHienCreateRequest = {
      congViec: createCongViec as "DANG_KY_DE_TAI" | "NOP_DE_CUONG",
      thoiGianBatDau: toISO(createStartDate),
      thoiGianKetThuc: toISO(createEndDate),
      dotBaoVeId: Number(createDotId),
    };

    setSubmittingCreate(true);
    try {
      const res = await createThoiGianThucHien(payload);
      if (res.result) {
        toast.success("Tạo công việc thành công");
        setOpenCreate(false);
        setCreateStartDate(undefined);
        setCreateEndDate(undefined);
        setCreateDotId("");
        loadData();
      } else {
        toast.error(res.message || "Không thể tạo công việc");
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        const data = err.response?.data as
          | { code?: number; message?: string }
          | undefined;
        if (data?.code === 1035) {
          toast.error("Công việc đã tồn tại trong đợt bảo vệ.");
        } else if (data?.code === 1034) {
          toast.error("Thời gian bắt đầu và kết thúc không hợp lệ.");
        } else {
          toast.error(
            data?.message || err.message || "Không thể tạo công việc"
          );
        }
      } else {
        toast.error("Không thể tạo công việc");
      }
    } finally {
      setSubmittingCreate(false);
    }
  };

  /* ====== Update ====== */
  const handleUpdate = async () => {
    if (!editing) return;

    // 🔒 CHẶN ID RỖNG NGAY Ở UI
    const idNum = Number(editing.id);
    if (!Number.isFinite(idNum)) {
      toast.error("Thiếu ID công việc, không thể cập nhật.");
      return;
    }

    if (!editDotId)
      return toast.error("Thiếu đợt bảo vệ của công việc hiện tại");
    if (!editStartDate || !editEndDate)
      return toast.error("Vui lòng chọn thời gian bắt đầu/kết thúc");
    if (editStartDate > editEndDate)
      return toast.error(
        "Thời gian bắt đầu phải nhỏ hơn hoặc bằng thời gian kết thúc"
      );

    const payload: ThoiGianThucHienCreateRequest = {
      congViec: editCongViec as "DANG_KY_DE_TAI" | "NOP_DE_CUONG",
      thoiGianBatDau: toISO(editStartDate),
      thoiGianKetThuc: toISO(editEndDate),
      dotBaoVeId: Number(editDotId),
    };

    setSubmittingEdit(true);
    try {
      const res = await updateThoiGianThucHien(idNum, payload);
      if (res.result) {
        toast.success("Cập nhật công việc thành công");
        setOpenEdit(false);
        setEditing(null);
        loadData();
      } else {
        toast.error(res.message || "Không thể cập nhật công việc");
      }
    } catch (err) {
      if (err instanceof AxiosError) {
        const data = err.response?.data as
          | { code?: number; message?: string }
          | undefined;
        if (data?.code === 1035) {
          toast.error("Công việc đã tồn tại trong đợt bảo vệ.");
        } else if (data?.code === 1034) {
          toast.error("Thời gian bắt đầu và kết thúc không hợp lệ.");
        } else {
          toast.error(
            data?.message || err.message || "Không thể cập nhật công việc"
          );
        }
      } else {
        // nếu service ném Error "Invalid thoiGianThucHienId"
        toast.error((err as Error).message || "Không thể cập nhật công việc");
      }
    } finally {
      setSubmittingEdit(false);
    }
  };

  return (
    <div>
      <h1 className="text-3xl text-center mt-10 font-bold mb-4">
        Thời gian thực hiện
      </h1>

      <div className="flex items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <Dialog open={openCreate} onOpenChange={setOpenCreate}>
            <DialogTrigger asChild>
              <Button
                onClick={openCreateDialog}
                className="bg-[#457B9D] text-white hover:bg-[#35607a] flex items-center gap-2"
              >
                <Plus className="w-4 h-4" />
                Thêm công việc
              </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-[520px] overflow-visible bg-white border-none rounded-lg shadow-lg">
              <DialogHeader>
                <DialogTitle className="text-center">
                  Thêm công việc
                </DialogTitle>
              </DialogHeader>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {/* Loại công việc */}
                <div>
                  <label className="text-sm font-medium">Loại công việc</label>
                  <Select
                    value={createCongViec}
                    onValueChange={setCreateCongViec}
                  >
                    <SelectTrigger className="mt-1 w-full h-10 bg-white border border-gray-300 focus:ring-2 focus:ring-[#2F80ED]">
                      <SelectValue placeholder="Chọn công việc" />
                    </SelectTrigger>
                    <SelectContent
                      position="popper"
                      sideOffset={4}
                      align="start"
                      className="z-[80] w-[var(--radix-select-trigger-width)] bg-white max-h-36 overflow-y-auto overflow-x-hidden"
                    >
                      <SelectItem
                        value="NOP_DE_CUONG"
                        className="whitespace-normal break-words"
                      >
                        Nộp đề cương
                      </SelectItem>
                      <SelectItem
                        value="DANG_KY_DE_TAI"
                        className="whitespace-normal break-words"
                      >
                        Đăng ký đồ án
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                {/* Đợt bảo vệ */}
                <div>
                  <label className="text-sm font-medium">Đợt bảo vệ</label>
                  <DotBaoVeCombobox
                    items={dotBaoVeRows}
                    value={createDotId}
                    onChange={setCreateDotId}
                    loading={loadingDots}
                    placeholder="-- Chọn đợt bảo vệ --"
                  />
                </div>

                {/* Ngày bắt đầu/kết thúc */}
                <div>
                  <label className="text-sm font-medium">
                    Thời gian bắt đầu
                  </label>
                  <DateInput
                    value={createStartDate}
                    onChange={setCreateStartDate}
                  />
                </div>
                <div>
                  <label className="text-sm font-medium">
                    Thời gian kết thúc
                  </label>
                  <DateInput
                    value={createEndDate}
                    onChange={setCreateEndDate}
                  />
                </div>

                <DialogFooter className="col-span-1 md:col-span-2 mt-2 flex gap-2">
                  <DialogClose asChild>
                    <Button
                      type="button"
                      className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]"
                      disabled={submittingCreate}
                    >
                      Trở về
                    </Button>
                  </DialogClose>
                  <Button
                    type="button"
                    className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                    onClick={handleCreate}
                    disabled={submittingCreate}
                  >
                    {submittingCreate ? "Đang lưu..." : "Lưu"}
                  </Button>
                </DialogFooter>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            {/* Bỏ cột ID */}
            <TableHead className="text-center font-semibold border border-gray-300">
              Công việc
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Thời gian bắt đầu
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Thời gian kết thúc
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Đợt bảo vệ
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Hành động
            </TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {loading && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={colCount}
              >
                Đang tải dữ liệu...
              </TableCell>
            </TableRow>
          )}

          {!loading && data.length === 0 && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={colCount}
              >
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}

          {!loading &&
            data.map((row) => (
              <TableRow
                key={row.id}
                className="hover:bg-gray-50 transition-colors"
              >
                <TableCell className="text-center border border-gray-300">
                  {labelCongViec(row.congViec)}
                </TableCell>
                <TableCell className="text-center border border-gray-300">
                  {toVNDate(row.thoiGianBatDau)}
                </TableCell>
                <TableCell className="text-center border border-gray-300">
                  {toVNDate(row.thoiGianKetThuc)}
                </TableCell>
                <TableCell className="text-center border border-gray-300">
                  {row.tenDotBaoVe}
                </TableCell>
                <TableCell className="text-center border border-gray-300">
                  <Dialog open={openEdit} onOpenChange={setOpenEdit}>
                    <Button
                      size="sm"
                      variant="outline"
                      className="border border-gray-300"
                      onClick={() => openEditDialog(row)}
                      title="Sửa"
                    >
                      <Pencil className="w-4 h-4" />
                    </Button>

                    <DialogContent className="sm:max-w-[520px] overflow-visible bg-white border-none rounded-lg shadow-lg">
                      <DialogHeader>
                        <DialogTitle className="text-center">
                          Sửa công việc
                        </DialogTitle>
                      </DialogHeader>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                        {/* Loại công việc */}
                        <div>
                          <label className="text-sm font-medium">
                            Loại công việc
                          </label>
                          <Select
                            value={editCongViec}
                            onValueChange={setEditCongViec}
                          >
                            <SelectTrigger className="mt-1 w-full h-10 bg-white border border-gray-300 focus:ring-2 focus:ring-[#2F80ED]">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent
                              position="popper"
                              sideOffset={4}
                              align="start"
                              className="z-[80] w-[var(--radix-select-trigger-width)] bg-white max-h-36 overflow-y-auto overflow-x-hidden"
                            >
                              <SelectItem
                                value="NOP_DE_CUONG"
                                className="whitespace-normal break-words"
                              >
                                Nộp đề cương
                              </SelectItem>
                              <SelectItem
                                value="DANG_KY_DE_TAI"
                                className="whitespace-normal break-words"
                              >
                                Đăng ký đồ án
                              </SelectItem>
                            </SelectContent>
                          </Select>
                        </div>

                        {/* Ngày bắt đầu/kết thúc */}
                        <div>
                          <label className="text-sm font-medium">
                            Thời gian bắt đầu
                          </label>
                          <DateInput
                            value={editStartDate}
                            onChange={setEditStartDate}
                          />
                        </div>
                        <div>
                          <label className="text-sm font-medium">
                            Thời gian kết thúc
                          </label>
                          <DateInput
                            value={editEndDate}
                            onChange={setEditEndDate}
                          />
                        </div>

                        {/* Đợt bảo vệ: disabled (giữ id) */}
                        <div>
                          <label className="text-sm font-medium">
                            Đợt bảo vệ
                          </label>
                          <DotBaoVeCombobox
                            items={dotBaoVeRows}
                            value={editDotId}
                            onChange={setEditDotId}
                            loading={loadingDots}
                            placeholder={
                              editing?.tenDotBaoVe || "-- Chọn đợt bảo vệ --"
                            }
                            disabled
                          />
                        </div>

                        <DialogFooter className="col-span-1 md:col-span-2 mt-2 flex gap-2">
                          <DialogClose asChild>
                            <Button
                              type="button"
                              className="bg-[#BFBFBF] text-white hover:bg-[#a6a6a6]"
                              disabled={submittingEdit}
                            >
                              Trở về
                            </Button>
                          </DialogClose>
                          <Button
                            type="button"
                            className="bg-[#457B9D] text-white hover:bg-[#35607a]"
                            onClick={handleUpdate}
                            disabled={
                              submittingEdit ||
                              !editDotId ||
                              loadingDots ||
                              !Number.isFinite(Number(editing?.id))
                            }
                          >
                            {submittingEdit ? "Đang lưu..." : "Lưu thay đổi"}
                          </Button>
                        </DialogFooter>
                      </div>
                    </DialogContent>
                  </Dialog>
                </TableCell>
              </TableRow>
            ))}
        </TableBody>
      </Table>

      <div className="flex justify-end mx-auto mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e) => {
              e.preventDefault();
              if (page > 0) setPage(page - 1);
            }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
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
              className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 ${
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
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-300 bg-gray-100 ${
              page + 1 >= totalPages
                ? "pointer-events-none opacity-50"
                : "hover:bg-gray-200"
            }`}
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}

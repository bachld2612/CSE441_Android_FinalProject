import { useEffect, useState } from 'react';
import { CheckCircle, XCircle, Eye } from 'lucide-react';
import {
  getDeTaiApproval,
  approveDeTai,
  rejectDeTai,
  type DeTai,
} from '@/services/deTai.service';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight, Search } from 'lucide-react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { downloadFile } from '@/lib/downloadFile';
import { toast } from 'react-toastify';
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from '@/components/ui/breadcrumb';

export default function DeTaiApprovalPage() {
  const [data, setData] = useState<DeTai[]>([]);
  const [page, setPage] = useState(0);
  const size = 7;
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [q, setQ] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const [selectedDeTai, setSelectedDeTai] = useState<DeTai | null>(null);

  // Dialog lý do
  const [reason, setReason] = useState('');
  const [actionType, setActionType] = useState<'APPROVE' | 'REJECT' | null>(
    null,
  );

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getDeTaiApproval({
        page,
        size,
        sort: 'maSV,asc',
      });

      let filtered = res.content;
      if (statusFilter !== 'ALL') {
        filtered = filtered.filter((dt) => dt.trangThai === statusFilter);
      }

      setData(filtered);
      setTotalPages(res.totalPages);
    } catch (e: any) {
      toast.error('Không thể tải danh sách đề tài');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, statusFilter]);

  const renderStatus = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return (
          <span className="px-2 py-1 rounded-full bg-green-100 text-green-700 text-sm font-medium">
            Đã duyệt
          </span>
        );
      case 'PENDING':
        return (
          <span className="px-2 py-1 rounded-full bg-yellow-100 text-yellow-700 text-sm font-medium">
            Chờ xét duyệt
          </span>
        );
      case 'CANCELED':
      case 'REJECTED':
        return (
          <span className="px-2 py-1 rounded-full bg-red-100 text-red-700 text-sm font-medium">
            Đã từ chối
          </span>
        );
      default:
        return status;
    }
  };

  const handleConfirm = async () => {
    if (!selectedDeTai || !actionType) return;
    try {
      if (actionType === 'APPROVE') {
        await approveDeTai(selectedDeTai.idDeTai, reason);
        toast.success('Duyệt đề tài thành công');
      } else {
        await rejectDeTai(selectedDeTai.idDeTai, reason);
        toast.success('Từ chối đề tài thành công');
      }
      setReason('');
      setActionType(null);
      setSelectedDeTai(null);
      loadData();
    } catch (e: any) {
      toast.error('Thao tác thất bại');
    }
  };

  return (
    <div>

      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="#">
              Đề tài
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-6">
        Danh sách đề tài
      </h1>

      {/* 🔹 Lọc bên trái, tìm kiếm bên phải */}
      <div className="flex justify-between items-center mb-4">
        {/* Bộ lọc trạng thái (trái) */}
        <Select value={statusFilter} onValueChange={setStatusFilter}>
          <SelectTrigger className="w-[180px] border border-gray-400 bg-white text-gray-800 font-medium">
            <SelectValue placeholder="Chọn trạng thái" />
          </SelectTrigger>
          <SelectContent className="bg-white border border-gray-400">
            <SelectItem value="ALL">Tất cả</SelectItem>
            <SelectItem value="PENDING">Chờ xét duyệt</SelectItem>
            <SelectItem value="ACCEPTED">Đã duyệt</SelectItem>
            <SelectItem value="CANCELED">Đã từ chối</SelectItem>
          </SelectContent>
        </Select>

        {/* Form tìm kiếm (phải) */}
        <form
          className="flex items-center gap-1"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            loadData();
          }}
        >
          <Input
            type="text"
            placeholder="Tìm kiếm đề tài..."
            className="w-[240px] border-gray-300 h-10"
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

      {/* Bảng dữ liệu */}
      <Table className="mt-2 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border border-gray-300">
              STT
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Mã SV
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Họ tên
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Lớp
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              SĐT
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Tên đề tài
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Trạng thái
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Hành động
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {!loading && data.length === 0 && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={8}
              >
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}
          {data.map((dt, i) => (
            <TableRow key={dt.maSV + dt.tenDeTai}>
              <TableCell className="text-center border border-gray-300">
                {page * size + i + 1}
              </TableCell>
              <TableCell className="text-center border border-gray-300 font-medium">
                {dt.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dt.hoTen}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dt.tenLop}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dt.soDienThoai ?? '-'}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dt.tenDeTai}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {renderStatus(dt.trangThai)}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                <div className="flex gap-3 justify-center">
                  {dt.trangThai === 'PENDING' && (
                    <>
                      {/* Duyệt */}
                      <CheckCircle
                        className="w-5 h-5 text-green-500 cursor-pointer hover:scale-110 transition-transform"
                        onClick={() => {
                          setSelectedDeTai(dt);
                          setActionType('APPROVE');
                        }}
                      />

                      {/* Từ chối */}
                      <XCircle
                        className="w-5 h-5 text-red-500 cursor-pointer hover:scale-110 transition-transform"
                        onClick={() => {
                          setSelectedDeTai(dt);
                          setActionType('REJECT');
                        }}
                      />
                    </>
                  )}

                  {/* Xem chi tiết */}
                  <Eye
                    className="w-5 h-5 text-blue-500 cursor-pointer hover:scale-110 transition-transform"
                    onClick={() => setSelectedDeTai(dt)}
                  />
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Phân trang */}
      <div className="flex justify-end mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e) => {
              e.preventDefault();
              if (page > 0) setPage(page - 1);
            }}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
              page === 0
                ? 'pointer-events-none opacity-50'
                : 'hover:bg-gray-200'
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
                  ? 'bg-[#2F80ED] text-white font-semibold'
                  : 'bg-gray-100 hover:bg-gray-200'
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
                ? 'pointer-events-none opacity-50'
                : 'hover:bg-gray-200'
            }`}
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Modal chi tiết */}
      <Dialog
        open={!!selectedDeTai && actionType === null}
        onOpenChange={() => setSelectedDeTai(null)}
      >
        <DialogContent className="max-w-lg bg-white border border-gray-400 shadow-md">
          <DialogHeader>
            <DialogTitle className="text-lg font-bold text-gray-900">
              Chi tiết đề tài
            </DialogTitle>
          </DialogHeader>
          {selectedDeTai && (
            <div className="space-y-2 text-gray-800 font-medium">
              <p>
                <strong>Mã SV:</strong> {selectedDeTai.maSV}
              </p>
              <p>
                <strong>Họ tên:</strong> {selectedDeTai.hoTen}
              </p>
              <p>
                <strong>Lớp:</strong> {selectedDeTai.tenLop}
              </p>
              <p>
                <strong>SĐT:</strong> {selectedDeTai.soDienThoai}
              </p>
              <p>
                <strong>Tên đề tài:</strong> {selectedDeTai.tenDeTai}
              </p>
              <p>
                <strong>Tổng quan:</strong>{' '}
                {selectedDeTai.tongQuanDeTaiUrl ? (
                  <button
                    onClick={() =>
                      downloadFile(
                        selectedDeTai.tongQuanDeTaiUrl || '',
                        selectedDeTai.tongQuanFilename || 'TongQuanDeTai.pdf',
                      )
                    }
                    className="text-blue-600 underline font-medium"
                  >
                    {selectedDeTai.tongQuanFilename || 'File tổng quan'}
                  </button>
                ) : (
                  <span className="text-gray-500 italic">Không có file</span>
                )}
              </p>
              <p>
                <strong>Trạng thái:</strong>{' '}
                {renderStatus(selectedDeTai.trangThai)}
              </p>

              {/* 🔹 Nhận xét (chỉ hiển thị khi đã duyệt hoặc từ chối) */}
              {(selectedDeTai.trangThai === 'ACCEPTED' ||
                selectedDeTai.trangThai === 'REJECTED') && (
                <p>
                  <strong>Nhận xét:</strong>{' '}
                  {selectedDeTai.nhanXet ? (
                    <span className="text-gray-700">{selectedDeTai.nhanXet}</span>
                  ) : (
                    <span className="text-gray-500 italic">
                      Không có nhận xét
                    </span>
                  )}
                </p>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Modal nhập lý do */}
      <Dialog
        open={!!selectedDeTai && !!actionType}
        onOpenChange={() => {
          setActionType(null);
          setSelectedDeTai(null);
        }}
      >
        <DialogContent className="max-w-md bg-white border border-gray-400 shadow-md">
          <DialogHeader>
            <DialogTitle className="text-lg font-bold text-gray-900">
              {actionType === 'APPROVE' ? 'Duyệt đề tài' : 'Từ chối đề tài'}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <Input
              type="text"
              placeholder="Nhập lý do..."
              value={reason}
              onChange={(e) => setReason(e.target.value)}
            />
            <div className="flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  setReason('');
                  setActionType(null);
                  setSelectedDeTai(null);
                }}
              >
                Hủy
              </Button>
              <Button
                className={
                  actionType === 'APPROVE'
                    ? 'bg-green-500 hover:bg-green-600 text-white'
                    : 'bg-red-500 hover:bg-red-600 text-white'
                }
                onClick={handleConfirm}
              >
                Xác nhận
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

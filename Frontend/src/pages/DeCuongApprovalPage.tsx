import { useEffect, useState } from 'react';
import {
  CheckCircle,
  XCircle,
  Eye,
  ChevronLeft,
  ChevronRight,
  Search,
} from 'lucide-react';
import {
  getDeCuongApproval,
  approveDeCuong,
  rejectDeCuong,
  type DeCuong,
} from '@/services/deCuong.service';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
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
import { toast } from 'react-toastify';

export default function DeCuongApprovalPage() {
  const [data, setData] = useState<DeCuong[]>([]);
  const [page, setPage] = useState(0);
  const size = 7;
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [q, setQ] = useState('');
  const [statusFilter, setStatusFilter] = useState<
    'ALL' | 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELED'
  >('ALL');

  const [selected, setSelected] = useState<DeCuong | null>(null);
  const [reason, setReason] = useState('');
  const [actionType, setActionType] = useState<'APPROVE' | 'REJECT' | null>(
    null,
  );

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getDeCuongApproval({
        page,
        size,
        sort: 'updatedAt,DESC',
      });
      let filtered = res.content;

      if (statusFilter !== 'ALL') {
        if (statusFilter === 'REJECTED') {
          // Gộp cả REJECTED và CANCELED vào “Đã từ chối”
          filtered = filtered.filter(
            (r) => r.trangThai === 'REJECTED' || r.trangThai === 'CANCELED',
          );
        } else {
          filtered = filtered.filter((r) => r.trangThai === statusFilter);
        }
      }

      const ql = q.trim().toLowerCase();
      if (ql) {
        filtered = filtered.filter(
          (r) =>
            r.maSV?.toLowerCase().includes(ql) ||
            r.hoTenSinhVien?.toLowerCase().includes(ql) ||
            r.tenDeTai?.toLowerCase().includes(ql),
        );
      }

      setData(filtered);
      setTotalPages(res.totalPages);
    } catch {
      toast.error('Không thể tải danh sách đề cương');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, statusFilter]); // eslint-disable-line

  const renderStatus = (st: string) => {
    switch (st) {
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
      case 'REJECTED':
      case 'CANCELED':
        return (
          <span className="px-2 py-1 rounded-full bg-red-100 text-red-700 text-sm font-medium">
            Đã từ chối
          </span>
        );
      default:
        return st;
    }
  };

  const handleConfirm = async () => {
    if (!selected || !actionType) return;
    try {
      if (actionType === 'APPROVE') {
        await approveDeCuong(selected.id); // Không gửi reason
        toast.success('Duyệt đề cương thành công');
      } else {
        await rejectDeCuong(selected.id, reason); // Bắt buộc reason
        toast.success('Từ chối đề cương thành công');
      }
      setReason('');
      setActionType(null);
      setSelected(null);
      loadData();
    } catch {
      toast.error('Thao tác thất bại');
    }
  };

  return (
    <div>
      <h1 className="text-3xl text-center mt-10 font-bold mb-6">
        Danh sách đề cương
      </h1>

      {/* Lọc trái / tìm kiếm phải */}
      <div className="flex justify-between items-center mb-4">
        <Select
          value={statusFilter}
          onValueChange={(v: any) => setStatusFilter(v)}
        >
          <SelectTrigger className="w-[180px] border border-gray-400 bg-white text-gray-800 font-medium">
            <SelectValue placeholder="Chọn trạng thái" />
          </SelectTrigger>
          <SelectContent className="bg-white border border-gray-400">
            <SelectItem value="ALL">Tất cả</SelectItem>
            <SelectItem value="PENDING">Chờ xét duyệt</SelectItem>
            <SelectItem value="ACCEPTED">Đã duyệt</SelectItem>
            <SelectItem value="REJECTED">Đã từ chối</SelectItem>
          </SelectContent>
        </Select>

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
            placeholder="Tìm theo mã SV / họ tên / tên đề tài..."
            className="w-[300px] border-gray-300 h-10"
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
              Họ tên SV
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              GV hướng dẫn
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Tên đề tài
            </TableHead>
            <TableHead className="text-center font-semibold border border-gray-300">
              Số lần nộp
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

          {data.map((dc, i) => (
            <TableRow key={dc.id}>
              <TableCell className="text-center border border-gray-300">
                {page * size + i + 1}
              </TableCell>
              <TableCell className="text-center border border-gray-300 font-medium">
                {dc.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dc.hoTenSinhVien}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dc.hoTenGiangVien ?? '-'}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dc.tenDeTai}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {dc.soLanNop ?? 0}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {renderStatus(dc.trangThai)}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                <div className="flex gap-3 justify-center">
                  {dc.trangThai === 'PENDING' && (
                    <>
                      <CheckCircle
                        className="w-5 h-5 text-green-500 cursor-pointer hover:scale-110 transition-transform"
                        onClick={() => {
                          setReason('');
                          setSelected(dc);
                          setActionType('APPROVE');
                        }}
                      />
                      <XCircle
                        className="w-5 h-5 text-red-500 cursor-pointer hover:scale-110 transition-transform"
                        onClick={() => {
                          setReason('');
                          setSelected(dc);
                          setActionType('REJECT');
                        }}
                      />
                    </>
                  )}
                  <Eye
                    className="w-5 h-5 text-blue-500 cursor-pointer hover:scale-110 transition-transform"
                    onClick={() => setSelected(dc)}
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
        open={!!selected && actionType === null}
        onOpenChange={() => setSelected(null)}
      >
        <DialogContent className="max-w-lg bg-white border border-gray-400 shadow-md">
          <DialogHeader>
            <DialogTitle className="text-lg font-bold text-gray-900">
              Chi tiết đề cương
            </DialogTitle>
          </DialogHeader>

          {selected && (
            <div className="space-y-2 text-gray-800 font-medium">
              <p>
                <strong>Mã SV:</strong> {selected.maSV}
              </p>
              <p>
                <strong>Họ tên SV:</strong> {selected.hoTenSinhVien}
              </p>
              <p>
                <strong>GV hướng dẫn:</strong> {selected.hoTenGiangVien ?? '-'}
              </p>
              <p>
                <strong>Tên đề tài:</strong> {selected.tenDeTai}
              </p>
              <p>
                <strong>Link đề cương:</strong>{' '}
                {selected.deCuongUrl ? (
                  <a
                    href={selected.deCuongUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-600 underline font-medium"
                  >
                    Link đề cương
                  </a>
                ) : (
                  <span className="text-gray-500 italic">Không có link</span>
                )}
              </p>
              <p>
                <strong>Trạng thái:</strong> {renderStatus(selected.trangThai)}
              </p>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Modal xác nhận / nhập lý do */}
      <Dialog
        open={!!selected && !!actionType}
        onOpenChange={() => {
          setActionType(null);
          setSelected(null);
          setReason('');
        }}
      >
        <DialogContent className="max-w-md bg-white border border-gray-400 shadow-md">
          <DialogHeader>
            <DialogTitle className="text-lg font-bold text-gray-900">
              {actionType === 'APPROVE' ? 'Duyệt đề cương' : 'Từ chối đề cương'}
            </DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            {/* Chỉ hiển thị ô lý do khi TỪ CHỐI */}
            {actionType === 'REJECT' && (
              <Input
                type="text"
                placeholder="Nhập nhận xét/lý do từ chối..."
                value={reason}
                onChange={(e) => setReason(e.target.value)}
              />
            )}

            <div className="flex justify-end gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  setReason('');
                  setActionType(null);
                  setSelected(null);
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
                disabled={actionType === 'REJECT' && reason.trim() === ''}
                title={
                  actionType === 'REJECT' && reason.trim() === ''
                    ? 'Vui lòng nhập nhận xét'
                    : ''
                }
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

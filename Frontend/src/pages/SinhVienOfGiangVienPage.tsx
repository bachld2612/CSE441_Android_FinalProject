import { useEffect, useState } from "react";
import { getSinhVienOfGiangVien, type SinhVienOfGiangVien } from "@/services/sinhVien.service";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight, Search } from "lucide-react";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";

export default function SinhVienOfGiangVienPage() {
  const [data, setData] = useState<SinhVienOfGiangVien[]>([]);
  const [page, setPage] = useState(0);     // Spring pageable: 0-based
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // optional search text (nếu BE support)
  const [q, setQ] = useState("");

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getSinhVienOfGiangVien({
        page,
        size,
        sort: "maSV,asc", // đổi nếu BE yêu cầu khác
        // nếu BE hỗ trợ search, thêm q vào params => sửa ở service
      });
      setData(res.content);
      setTotalPages(res.totalPages);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, size]);

  return (
    <div>
      <h1 className="text-3xl text-center mt-10 font-bold mb-4">Sinh viên đang đăng ký</h1>

      {/* Search box (chỉ hoạt động nếu BE hỗ trợ) */}
      <form
        className="flex items-center gap-1 justify-end"
        onSubmit={(e) => { e.preventDefault(); setPage(0); loadData(); }}
      >
        <Input
          type="text"
          placeholder="Tìm kiếm sinh viên..."
          className="w-[300px] border-gray-300 h-10"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <Button type="submit" className="h-10 border-gray-300" variant="outline">
          <Search />
        </Button>
      </form>

      {/* Table */}
      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
        <TableHeader>
          <TableRow className="bg-gray-100">
            <TableHead className="text-center font-semibold border">STT</TableHead>
            <TableHead className="text-center font-semibold border">Mã SV</TableHead>
            <TableHead className="text-center font-semibold border">Họ tên</TableHead>
            <TableHead className="text-center font-semibold border">Lớp</TableHead>
            <TableHead className="text-center font-semibold border">SĐT</TableHead>
            <TableHead className="text-center font-semibold border">Tên đề tài</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {!loading && data.length === 0 && (
            <TableRow>
              <TableCell className="text-center border" colSpan={6}>Không có dữ liệu</TableCell>
            </TableRow>
          )}
          {data.map((sv, i) => (
            <TableRow key={sv.maSV} className="hover:bg-gray-50 transition-colors">
              <TableCell className="text-center border">{page * size + i + 1}</TableCell>
              <TableCell className="text-center border font-medium">{sv.maSV}</TableCell>
              <TableCell className="text-center border">{sv.hoTen}</TableCell>
              <TableCell className="text-center border">{sv.tenLop}</TableCell>
              <TableCell className="text-center border">{sv.soDienThoai ?? "-"}</TableCell>
              <TableCell className="text-center border">{sv.tenDeTai ?? "-"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Pagination (style giống màn cũ) */}
      <div className="flex justify-end mx-auto mt-6">
        <div className="flex items-center gap-2">
          <button
            onClick={(e)=>{e.preventDefault(); if(page>0) setPage(page-1);}}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
              page === 0 ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
          >
            <ChevronLeft className="w-4 h-4" />
          </button>

          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={(e)=>{e.preventDefault(); setPage(i);}}
              className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 ${
                page === i ? "bg-[#2F80ED] text-white font-semibold" : "bg-gray-100 hover:bg-gray-200"
              }`}
            >
              {i + 1}
            </button>
          ))}

          <button
            onClick={(e)=>{e.preventDefault(); if(page + 1 < totalPages) setPage(page + 1);}}
            className={`h-8 w-8 flex items-center justify-center rounded-full border border-gray-200 bg-gray-100 ${
              page + 1 >= totalPages ? "pointer-events-none opacity-50" : "hover:bg-gray-200"
            }`}
          >
            <ChevronRight className="w-4 h-4" />
          </button>

          <select
            className="border rounded px-2 py-1 ml-2"
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
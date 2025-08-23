// src/pages/SinhVienOfGiangVienPage.tsx
import { useEffect, useState } from "react";
import {
  getSinhVienOfGiangVien,
  type SinhVienOfGiangVien,
} from "@/services/sinhVien.service";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight, Search } from "lucide-react";
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
import { downloadFile } from "@/lib/downloadFile";

// Mở rộng type để chắc chắn có cvUrl từ BE (nếu service chưa có)
type Row = SinhVienOfGiangVien & {
  cvUrl?: string;
  cvFilename?: string;
};

export default function SinhVienOfGiangVienPage() {
  const [data, setData] = useState<Row[]>([]);
  const [page, setPage] = useState(0);
  const size = 7;
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [q, setQ] = useState("");

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await getSinhVienOfGiangVien({
        page,
        size,
        sort: "maSV,asc",
        // Nếu BE/Service hỗ trợ tìm kiếm theo q, truyền thêm param tại đây.
        // q,
      });
      setData(res.content as Row[]);
      setTotalPages(res.totalPages);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

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
              Sinh viên hướng dẫn
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-4">
        Sinh viên đang đăng ký
      </h1>

      <form
        className="flex items-center gap-1 justify-end"
        onSubmit={(e) => {
          e.preventDefault();
          setPage(0);
          loadData();
        }}
      >
        <Input
          type="text"
          placeholder="Tìm kiếm sinh viên..."
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

      <Table className="mt-6 rounded-lg overflow-hidden shadow-sm border border-gray-300">
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
              CV
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {!loading && data.length === 0 && (
            <TableRow>
              <TableCell
                className="text-center border border-gray-300"
                colSpan={7}
              >
                Không có dữ liệu
              </TableCell>
            </TableRow>
          )}
          {data.map((sv, i) => (
            <TableRow
              key={sv.maSV}
              className="hover:bg-gray-50 transition-colors"
            >
              <TableCell className="text-center border border-gray-300">
                {page * size + i + 1}
              </TableCell>
              <TableCell className="text-center border border-gray-300 font-medium">
                {sv.maSV}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {sv.hoTen}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {sv.tenLop}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {sv.soDienThoai ?? "-"}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {sv.tenDeTai ?? "-"}
              </TableCell>
              <TableCell className="text-center border border-gray-300">
                {sv.cvUrl ? (
                  <button
                    onClick={() =>
                      downloadFile(
                        sv.cvUrl!,
                        sv.cvFilename ||
                          `${sv.maSV ? sv.maSV : "sinhvien"}_CV.pdf`
                      )
                    }
                    className="text-blue-600 underline font-medium"
                  >
                    {sv.cvFilename || "CV.pdf"}
                  </button>
                ) : (
                  <span className="text-gray-500 italic">Không có CV</span>
                )}
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
    </div>
  );
}

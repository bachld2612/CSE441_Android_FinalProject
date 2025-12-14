import { useRef, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { createThongBao } from "@/services/thongBao.service";
import { toast } from "react-toastify";

const MAX_SIZE = 5 * 1024 * 1024; // 5MB

export default function ThongBaoCreatePage() {
  const [tieuDe, setTieuDe] = useState("");
  const [noiDung, setNoiDung] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const fileRef = useRef<HTMLInputElement | null>(null);

  // Chỉ nhận PDF & <= 5MB
  const validatePdf = (f: File) => {
    const isPdfMime = f.type === "application/pdf";
    const isPdfExt = f.name.toLowerCase().endsWith(".pdf");
    if (!isPdfMime && !isPdfExt) {
      toast.error("Chỉ cho phép tải lên tệp PDF (.pdf).");
      return false;
    }
    if (f.size > MAX_SIZE) {
      toast.error("Kích thước tệp tối đa 5MB.");
      return false;
    }
    return true;
  };

  const onFileChange: React.ChangeEventHandler<HTMLInputElement> = (e) => {
    const f = e.target.files?.[0] ?? null;
    if (!f) {
      setFile(null);
      return;
    }
    if (!validatePdf(f)) {
      if (fileRef.current) fileRef.current.value = "";
      setFile(null);
      return;
    }
    setFile(f);
  };

  const resetForm = () => {
    setTieuDe("");
    setNoiDung("");
    setFile(null);
    if (fileRef.current) fileRef.current.value = "";
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (file && !validatePdf(file)) return;

    try {
      setLoading(true);
      const res = await createThongBao({ tieuDe, noiDung, file });
      if (!res.result) throw new Error(res.message || "Tạo thông báo thất bại");

      toast.success("Tạo thông báo thành công");
      resetForm();
      window.dispatchEvent(new CustomEvent("thongbao:new"));
    } catch (err: any) {
      toast.error(err?.message || "Tạo thông báo thất bại");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pb-8">
      {/* Breadcrumb shadcn */}
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbPage>
            <BreadcrumbLink className="font-bold" href="#">
              Thông báo hệ thống
            </BreadcrumbLink>
          </BreadcrumbPage>
        </BreadcrumbList>
      </Breadcrumb>

      <h1 className="text-3xl text-center mt-10 font-bold mb-6">
        Thêm thông báo hệ thống
      </h1>

      <form
        onSubmit={onSubmit}
        className="max-w-2xl mx-auto space-y-4 bg-white border border-gray-200 rounded-lg p-4"
      >
        <div>
          <label className="block text-sm font-medium mb-1">Tiêu đề</label>
          <Input
            className="border border-gray-200"
            value={tieuDe}
            onChange={(e) => setTieuDe(e.target.value)}
            required
            placeholder="Nhập tiêu đề"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Nội dung</label>
          <textarea
            className="border border-gray-200 rounded-md w-full p-2 min-h-[160px]"
            value={noiDung}
            onChange={(e) => setNoiDung(e.target.value)}
            required
            placeholder="Nhập nội dung thông báo..."
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">
            Tệp đính kèm (đuôi PDF, tối đa 5MB)
          </label>
          <Input
            ref={fileRef}
            type="file"
            accept="application/pdf,.pdf"
            onChange={onFileChange}
            className="border border-gray-200"
          />
          <p className="text-xs text-gray-500 mt-1">
            Chỉ chấp nhận tệp PDF. Dung lượng tối đa 5MB.
          </p>
        </div>

        {/* Nút hành động */}
        <div className="flex gap-2 justify-end">
          {/* Làm mới: #BFBFBF */}
          <Button
            type="button"
            variant="outline"
            onClick={resetForm}
            className="bg-[#BFBFBF] hover:bg-[#a6a6a6] text-black border-0"
          >
            Làm mới
          </Button>

          {/* Tạo thông báo: #457B9D */}
          <Button
            type="submit"
            disabled={loading}
            className="bg-[#457B9D] hover:bg-[#3e6e8d] text-white border-0 disabled:opacity-50"
          >
            {loading ? "Đang tạo..." : "Tạo thông báo"}
          </Button>
        </div>
      </form>
    </div>
  );
}

// src/pages/TaiKhoanInfoPage.tsx
import { useEffect, useState } from "react";
import { getMyInfo, type InfoResponse } from "@/services/auth.service";

const ROLE_LABEL: Record<string, string> = {
  TRO_LY_KHOA: "trợ lý khoa",
  TRUONG_BO_MON: "trưởng bộ môn",
  GIANG_VIEN: "giảng viên",
  ADMIN: "quản trị",
  SINH_VIEN: "sinh viên",
};

function toRoleLabel(role?: string) {
  if (!role) return "-";
  return ROLE_LABEL[role] ?? role;
}

export default function TaiKhoanInfoPage() {
  const [info, setInfo] = useState<InfoResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const res = await getMyInfo();
        const data = res.result ?? null;
        setInfo(data);
        if (data) localStorage.setItem("myInfo", JSON.stringify(data));
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) return <div>Đang tải...</div>;

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
      <div className="mb-3 text-base font-medium">Thông tin</div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
        <InfoItem label="Họ tên" value={info?.hoTen} />
        <InfoItem label="Email" value={info?.email} />
        <InfoItem label="Vai trò" value={toRoleLabel(String(info?.role ?? ""))} />
        <InfoItem label="Số điện thoại" value={info?.soDienThoai} />

        {/* Không hiển thị Lớp và Mã SV; Chỉ hiển thị Mã GV nếu có */}
        {info?.maGV && <InfoItem label="Mã GV" value={info.maGV} />}

        <InfoItem label="Ngành / Bộ môn" value={info?.nganh || info?.boMon} />
        <InfoItem label="Khoa" value={info?.khoa} />
        <InfoItem label="Học vị" value={info?.hocVi} />
        <InfoItem label="Học hàm" value={info?.hocHam} />
      </div>
    </div>
  );
}

function InfoItem({ label, value }: { label: string; value?: string }) {
  return (
    <div className="rounded border border-gray-200 bg-gray-50 px-3 py-2">
      <div className="text-xs text-gray-500">{label}</div>
      <div className="text-sm font-medium">{value || "-"}</div>
    </div>
  );
}

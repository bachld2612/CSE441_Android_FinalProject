import TrangChuComponent, {
  type TrangChuPageProps,
} from "@/components/TrangChuComponent";
import { useEffect, useState } from "react";

export default function TrangChuPage() {
  const [role, setRole] = useState(null);

  useEffect(() => {
    const storedInfo = localStorage.getItem("myInfo");
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        setRole(parsedInfo.role || null);
        console.log("Parsed role:", parsedInfo.role);
      } catch (error) {
        console.error("Lỗi parse myInfo:", error);
      }
    }
  }, []);

  const trangChuProps: TrangChuPageProps[] = [
    {
      href: "/do-an",
      name: "Đồ án",
      hidden: role === "ADMIN",
    },
    {
      href: role === "GIANG_VIEN" ? "/sinh-vien/huong-dan" : "/sinh-vien",
      name: "Sinh viên",
      hidden: false,
    },
    {
      href: "#",
      name: "Hội đồng",
      hidden: false,
    },
    {
      href: "/to-chuc",
      name: "Tổ chức",
      hidden: false,
    },
    {
      href: "#",
      name: "Thông tin",
      hidden: false,
    },
  ];

  return (
    <div className="grid grid-cols-3 gap-10 p-4 gap-y-20">
      {trangChuProps.map((item, index) => (
        <TrangChuComponent key={index} {...item} />
      ))}
    </div>
  );
}

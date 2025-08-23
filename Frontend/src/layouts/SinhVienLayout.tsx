import Header from "@/components/Header";
import { Outlet } from "react-router-dom";
import SidebarSinhVien from "@/components/SidebarSinhVien";
import { useEffect, useState } from "react";

export default function SinhVienLayout() {

  const [role, setRole] = useState(null);
  
    useEffect(() => {
      const storedInfo = localStorage.getItem("myInfo");
      if (storedInfo) {
        try {
          const parsedInfo = JSON.parse(storedInfo);
          setRole(parsedInfo.role.toLowerCase() || null);
          console.log("Parsed role:", parsedInfo.role);
        } catch (error) {
          console.error("Lỗi parse myInfo:", error);
        }
      }
    }, []);

  return (
    <div className="flex min-h-screen">
      {role !== "giang_vien" && <SidebarSinhVien />}
      <div className="flex-1 flex flex-col">
        <Header />
        <main className={`flex-1 p-6 mt-[64px] ${role !== "giang_vien" ? 'ml-64' : 'container mx-auto'}`}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}

import Header from "@/components/Header";
import { Outlet } from "react-router-dom";

export default function ThongBaoLayout() {
  return (
    <div className="min-h-screen bg-white">
      <Header />
      {/* Page content */}
      <main className="container mx-auto px-6 pt-16 pb-6">
        <Outlet />
      </main>
    </div>
  );
}

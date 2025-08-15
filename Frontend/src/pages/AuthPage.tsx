import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/authStore";

// src/pages/AuthPage.tsx
export default function AuthPage() {

  const handleLogout = async () => {
    const response = await useAuthStore.getState().logout();
    if (response.code === 1000) {
      window.location.href = "/login";
    }
  }
  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold">Trang bảo vệ - Chỉ xem khi đã login</h1>
      <Button onClick={handleLogout}>Logout</Button>
    </div>
  );
}

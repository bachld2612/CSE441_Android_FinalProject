import type { JSX } from "react";
import { useAuthStore } from "../stores/authStore";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children }: { children: JSX.Element }) {
  const isAuthenticated = useAuthStore((s) => s.token);
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
import { useEffect, useState, type JSX } from "react";
import { useAuthStore } from "../stores/authStore";
import { Navigate } from "react-router-dom";
import { Loader2 } from "lucide-react";

export default function ProtectedRoute({
  children,
}: {
  children: JSX.Element;
}) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const token = useAuthStore((s) => s.token);
  const introspect = useAuthStore((s) => s.introspect);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const result = await introspect();
        setIsAuthenticated(result);
        console.log("Auth check result:", result);
      } catch (error) {
        console.error("Auth check failed:", error);
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, [token, introspect]);

  if (isLoading) {
    return <Loader2 className="h-6 w-6 animate-spin text-primary"/>;
  }
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

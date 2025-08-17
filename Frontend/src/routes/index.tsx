// src/routes/index.tsx
import { createBrowserRouter, Navigate } from "react-router-dom";
import Hello from "@/pages/Hello";
import AuthPage from "@/pages/AuthPage";
import MainLayout from "@/layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import NotFound from "@/pages/NotFound";
import LoginPage from "@/pages/LoginPage";
import KhoaPage from "@/pages/KhoaPage";
import ToChucLayout from "@/layouts/ToChucLayout";
import BoMonPage from "@/pages/BoMonPage";
import NganhPage from "@/pages/NganhPage";
import LopPage from "@/pages/LopPage";

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },

  {
    path: "/",
    element: <MainLayout />,
    children: [
      { index: true, element: <Hello /> },
      {
        path: "auth",
        element: (
          <ProtectedRoute>
            <AuthPage />
          </ProtectedRoute>
        ),
      },
      { path: "*", element: <NotFound /> },
    ],
  },

  {
  path: "/to-chuc",
  element: (
    <ProtectedRoute>
      <ToChucLayout />
    </ProtectedRoute>
  ),
  children: [
    { index: true, element: <Navigate to="khoa" replace /> },
    { path: "khoa", element: <KhoaPage /> },
    { path: "bo-mon", element: <BoMonPage /> },
    { path: "nganh", element: <NganhPage /> },
    { path: "lop", element: <LopPage /> },
  ],
  },

]);

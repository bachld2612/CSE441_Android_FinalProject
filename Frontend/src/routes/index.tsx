import { createBrowserRouter, Navigate } from "react-router-dom";
import MainLayout from "@/layouts/MainLayout";
import ToChucLayout from "@/layouts/ToChucLayout";
import Hello from "@/pages/Hello";
import LoginPage from "@/pages/LoginPage";
import NotFound from "@/pages/NotFound";
import ProtectedRoute from "./ProtectedRoute";
import KhoaPage from "@/pages/KhoaPage";
import BoMonPage from "@/pages/BoMonPage";
import NganhPage from "@/pages/NganhPage";
import LopPage from "@/pages/LopPage";
import DotBaoVePage from "@/pages/DotBaoVePage";

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },

  {
    path: "/",
    element: <MainLayout />,
    handle: { breadcrumb: "Trang chủ" },
    children: [
      { index: true, element: <Hello />, handle: { breadcrumb: "Tổng quan" } },
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
    handle: { breadcrumb: "Quản lý tổ chức" },
    children: [
      { index: true, element: <Navigate to="khoa" replace /> },
      { path: "khoa", element: <KhoaPage />, handle: { breadcrumb: "Quản lý khoa" } },
      { path: "bo-mon", element: <BoMonPage />, handle: { breadcrumb: "Quản lý bộ môn" } },
      { path: "nganh", element: <NganhPage />, handle: { breadcrumb: "Quản lý ngành" } },
      { path: "lop", element: <LopPage />, handle: { breadcrumb: "Quản lý lớp" } },
      { path: "dot-do-an", element: <DotBaoVePage />, handle: { breadcrumb: "Quản lý đợt đồ án" } },
    ],
  },
]);

import { createBrowserRouter, Navigate } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';
import ToChucLayout from '@/layouts/ToChucLayout';
import NotFound from '@/pages/NotFound';
import ProtectedRoute from './ProtectedRoute';
import KhoaPage from '@/pages/KhoaPage';
import BoMonPage from '@/pages/BoMonPage';
import NganhPage from '@/pages/NganhPage';
import LopPage from '@/pages/LopPage';
import LoginPage from '@/pages/LoginPage';
import SinhVienLayout from '@/layouts/SinhVienLayout';
import DotBaoVePage from '@/pages/DotBaoVePage';
import SinhVienOfGiangVienPage from '@/pages/SinhVienOfGiangVienPage';
import DoAnLayout from '@/layouts/DoAnLayout';
import DeTaiApprovalPage from '@/pages/DeTaiApprovalPage';
import GiangVienLayout from "@/layouts/GiangVienLayout";
import GiangVienPage from "@/pages/GiangVienPage";
import DangKiGiangVienHuongDan from "@/pages/DangKiGiangVienHuongDan";
import SinhVienPage from '@/pages/SinhVienPage';
import DeCuongApprovalPage from '@/pages/DeCuongApprovalPage';
import ThongBaoCreatePage from '@/pages/ThongBaoCreatePage';
import ThongBaoLayout from '@/layouts/ThongBaoLayout';
import ThongBaoLatestPage from "@/pages/ThongBaoLatestPage";
import TrangChuPage from '@/pages/TrangChuPage';
import Hello from '@/pages/Hello';
import ThoiGianThucHienPage from '@/pages/ThoiGianThucHienPage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    path: '/sinh-vien',
    element: (
      <ProtectedRoute>
        <SinhVienLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <SinhVienPage/> },
      { path: 'huong-dan', element: <SinhVienOfGiangVienPage/> },
      { path: 'dang-ky-do-an', element: <Hello/> },
      { path: "gvhd", element: <DangKiGiangVienHuongDan/> },
    ]
  },

  {
    path: "/",
    element: <MainLayout />,
    handle: { breadcrumb: "Trang chủ" },
    children: [
      { index: true, element: <TrangChuPage />, handle: { breadcrumb: "Tổng quan" } },
      { path: "*", element: <NotFound /> },
    ],
  },
  {
    path: "/giang-vien",
    element: <GiangVienLayout />,
    children: [
      { index: true, element: <GiangVienPage /> },
    ],
  },

  {
    path: "/do-an",
    element: (
      <ProtectedRoute>
        <DoAnLayout />
      </ProtectedRoute>
    ),
    handle: { breadcrumb: "Quản lý đồ án" },
    children: [
      { index: true, element: <Navigate to="duyet-de-tai" replace /> },
      { path: "duyet-de-tai", element: <DeTaiApprovalPage />, handle: { breadcrumb: "Duyệt đề tài" } },
      { path: "duyet-de-cuong", element: <DeCuongApprovalPage />, handle: { breadcrumb: "Duyệt đề cương" } },
    ],
  },

  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <TrangChuPage /> },
      { path: '*', element: <NotFound /> },
    ],
  },

  {
    path: '/to-chuc',
    element: (
      <ProtectedRoute>
        <ToChucLayout />
      </ProtectedRoute>
    ),
    handle: { breadcrumb: 'Quản lý tổ chức' },
    children: [
      { index: true, element: <Navigate to="khoa" replace /> },
      {
        path: 'khoa',
        element: <KhoaPage />,
        handle: { breadcrumb: 'Quản lý khoa' },
      },
      {
        path: 'bo-mon',
        element: <BoMonPage />,
        handle: { breadcrumb: 'Quản lý bộ môn' },
      },
      {
        path: 'nganh',
        element: <NganhPage />,
        handle: { breadcrumb: 'Quản lý ngành' },
      },
      {
        path: 'lop',
        element: <LopPage />,
        handle: { breadcrumb: 'Quản lý lớp' },
      },
      {
        path: 'dot-do-an',
        element: <DotBaoVePage />,
        handle: { breadcrumb: 'Quản lý đợt đồ án' },
      },
      {
        path: 'thoi-gian-do-an',
        element: <ThoiGianThucHienPage />,
        handle: { breadcrumb: 'Quản lý thời gian đồ án' },
      }
    ],
  },
  {
  path: "/thong-bao",
  element: (
    <ProtectedRoute>
      <ThongBaoLayout />
    </ProtectedRoute>
  ),
  children: [
    { index: true, element: <ThongBaoCreatePage /> },
    { path: "moi-nhat", element: <ThongBaoLatestPage /> },
  ],
},
]);
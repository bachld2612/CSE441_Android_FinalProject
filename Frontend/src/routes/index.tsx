import { createBrowserRouter, } from "react-router-dom";
import Hello from "../pages/Hello";
import AuthPage from "../pages/AuthPage";
import MainLayout from "../layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import NotFound from "@/pages/NotFound";
import LoginPage from "@/pages/LoginPage";
import SinhVienLayout from "@/layouts/SinhVienLayout";
import StudentsEntry from "@/routes/SinhVienEntry";


export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    path: "/sinh-vien",
    element: <SinhVienLayout />,
    children: [
      { index: true, element: <StudentsEntry /> },
    ]
  },
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
]);
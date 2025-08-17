import { createBrowserRouter, } from "react-router-dom";
import Hello from "../pages/Hello";
import AuthPage from "../pages/AuthPage";
import MainLayout from "../layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import NotFound from "@/pages/NotFound";
import LoginPage from "@/pages/LoginPage";


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
]);
import { createBrowserRouter, } from "react-router-dom";
import Hello from "../pages/Hello";
import Login from "../pages/Login";
import AuthPage from "../pages/AuthPage";
import MainLayout from "../layouts/MainLayout";
import ProtectedRoute from "./ProtectedRoute";
import NotFound from "@/pages/NotFound";


export const router = createBrowserRouter([
  { path: "/login", element: <Login /> },
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
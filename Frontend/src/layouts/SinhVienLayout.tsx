import Header from '@/components/Header'
import TroLyKhoaSinhVienSideBar from '@/components/TroLyKhoaSinhVienSideBar'
import { Outlet } from 'react-router-dom'

export default function SinhVienLayout() {
  return (
    <div>
        <Header/>
        <TroLyKhoaSinhVienSideBar />
        {/* Page content */}
        <main className="container ml-[255px] flex-1 p-6 mt-[100px] mx-auto">
          <Outlet />
        </main>
      </div>
  )
}

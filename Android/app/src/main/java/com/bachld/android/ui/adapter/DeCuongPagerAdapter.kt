package com.bachld.android.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bachld.android.ui.view.decuong.DanhSachDeCuongFragment
import com.bachld.android.ui.view.decuong.NopDeCuongFragment

class DeCuongPagerAdapter(host: Fragment) : FragmentStateAdapter(host) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment =
        if (position == 0) NopDeCuongFragment() else DanhSachDeCuongFragment()
}

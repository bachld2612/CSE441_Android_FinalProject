package com.bachld.android.ui.view.decuong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bachld.android.databinding.FragmentDeCuongBinding
import com.bachld.android.ui.adapter.DeCuongPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class DeCuongFragment : Fragment() {
    private var _binding: FragmentDeCuongBinding? = null
    private val binding get() = _binding!!
    private val titles = listOf("Nộp đề cương", "Danh sách đề cương")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeCuongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = DeCuongPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = titles[pos]
        }.attach()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

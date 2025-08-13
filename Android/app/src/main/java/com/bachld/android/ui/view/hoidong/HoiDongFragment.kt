package com.bachld.android.ui.view.hoidong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bachld.android.databinding.FragmentHoiDongBinding
import com.bachld.android.ui.viewmodel.HoiDongViewModel

class HoiDongFragment: Fragment() {
    private var _binding: FragmentHoiDongBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HoiDongViewModel::class.java)

        _binding = FragmentHoiDongBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
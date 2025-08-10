package com.bachld.android.ui.hoidongfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bachld.android.R
import com.bachld.android.databinding.FragmentChiTietHoiDongBinding
import com.bachld.android.databinding.FragmentHoiDongBinding


class ChiTietHoiDongFragment : Fragment() {
    private var _binding: FragmentChiTietHoiDongBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChiTietHoiDongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // binding.textView.text = "Nội dung"
        // binding.button.setOnClickListener { ... }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.bachld.android.ui.view.sinhvien

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bachld.android.R
import com.bachld.android.databinding.FragmentThongTinSinhVienBinding

class ThongTinSinhVienFragment : Fragment(R.layout.fragment_thong_tin_sinh_vien) {
    private var _binding: FragmentThongTinSinhVienBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThongTinSinhVienBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
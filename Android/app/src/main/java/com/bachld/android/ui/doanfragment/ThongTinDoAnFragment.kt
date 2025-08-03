package com.bachld.android.ui.doanfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bachld.android.R
import com.bachld.android.databinding.FragmentThongTinDoAnBinding

class ThongTinDoAnFragment:Fragment() {
    private var _binding: FragmentThongTinDoAnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThongTinDoAnBinding.inflate(inflater, container, false)
        binding.btnDeNghiHoan.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_thong_tin_do_an, HoanDoAnFragment())
                .addToBackStack(null) // Cho phép quay lại Thông tin đồ án nếu bấm Back
                .commit()
        }
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
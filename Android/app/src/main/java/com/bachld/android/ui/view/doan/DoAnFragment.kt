package com.bachld.android.ui.view.doan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bachld.android.R
import com.bachld.android.databinding.FragmentDoAnBinding

class DoAnFragment: Fragment(R.layout.fragment_do_an) {
    private var _binding: FragmentDoAnBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        // Xóa toàn bộ fragment con trước đó trong container_thong_tin_do_an
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        // Luôn hiển thị lại ThongTinDoAnFragment
        if (childFragmentManager.findFragmentById(R.id.container_thong_tin_do_an) !is ThongTinDoAnFragment) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container_thong_tin_do_an, ThongTinDoAnFragment())
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoAnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container_thong_tin_do_an, ThongTinDoAnFragment())
                .commit()
        }

//        // Xử lý nút chuyển fragment con
//        binding.btnDangKyDeTai.setOnClickListener {
//            childFragmentManager.beginTransaction()
//                .replace(R.id.container_thong_tin_do_an, DangKyDoAnFragment())
//                .addToBackStack(null)
//                .commit()
//        }
//        // ... Các nút khác nếu cần
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun resetToThongTinDoAn() {
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        if (childFragmentManager.findFragmentById(R.id.container_thong_tin_do_an) !is ThongTinDoAnFragment) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container_thong_tin_do_an, ThongTinDoAnFragment())
                .commit()
        }
    }
}
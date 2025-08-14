package com.bachld.android.ui.view.doan

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bachld.android.databinding.FragmentDangKyDoAnBinding

class DangKyDoAnFragment: Fragment() {

    private var _binding: FragmentDangKyDoAnBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        // Nếu dùng Toolbar của Activity
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onPause() {
        super.onPause()
        // Ẩn lại nút back khi fragment này không còn ở top
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDangKyDoAnBinding.inflate(inflater, container, false)
        val gvList = listOf(
            "Chọn giảng viên hướng dẫn",
            "GV. Nguyễn Văn A",
            "GV. Trần Thị B",
            "GV. Lê Văn C"
        )
        val spinnerAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, gvList)
        binding.spnGvhd.adapter = spinnerAdapter

        // 2. Bắt sự kiện click upload file
        binding.frameUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Chọn file tổng quan"), 1001)
            binding.btnGuiDangKy.setOnClickListener {
                val gv = binding.spnGvhd.selectedItem?.toString() ?: ""
                val deTai = binding.edtTenDeTai.text?.toString() ?: ""
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
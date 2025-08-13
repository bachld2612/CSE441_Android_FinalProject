package com.bachld.android.ui.view.doan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bachld.android.databinding.FragmentHoanDoAnBinding

class HoanDoAnFragment: Fragment() {

    private var _binding: FragmentHoanDoAnBinding? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoanDoAnBinding.inflate(inflater, container, false)
        binding.frameUploadMinhChung.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Chọn tất cả loại file hoặc chỉ pdf/image/video/...
            startActivityForResult(Intent.createChooser(intent, "Chọn file minh chứng"), 1001)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            // Xử lý file ở đây, ví dụ đổi icon, đổi text hoặc upload file
        }
    }
}
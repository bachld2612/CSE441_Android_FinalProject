package com.bachld.android.ui.view.giangvien

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.databinding.FragmentDanhSachSinhVienHuongDanBinding
import com.bachld.android.ui.adapter.SinhVienAdapter
import com.bachld.android.ui.viewmodel.DanhSachSinhVienHDViewModel
import com.bachld.android.data.repository.impl.GiangVienRepositoryImpl
import kotlinx.coroutines.launch

class DanhSachSinhVienHuongDanFragment
    : Fragment(R.layout.fragment_danh_sach_sinh_vien_huong_dan) {

    private val adapter = SinhVienAdapter()

    // Nếu chưa DI, khởi tạo tạm
    private val view_model by lazy {
        val repo = GiangVienRepositoryImpl()
        DanhSachSinhVienHDViewModel(repo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDanhSachSinhVienHuongDanBinding.bind(view)

        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            view_model.update_query(text?.toString().orEmpty())
        }

        // Collect theo vòng đời STARTED
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    view_model.students_filtered.collect { list ->
                        adapter.submitList(list)
                    }
                }
                launch {
                    view_model.error.collect { msg ->
                        msg?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Gọi API: lấy all (không phân trang)
        view_model.load_all()
    }
}

package com.bachld.android.ui.view.giangvien

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
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
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.navigation.fragment.findNavController
import com.bachld.android.core.ApiConfig


class DanhSachSinhVienHuongDanFragment
    : Fragment(R.layout.fragment_danh_sach_sinh_vien_huong_dan) {

    private val adapter by lazy {
        SinhVienAdapter(
            onOpenCv = { cvRaw ->
                // Điều hướng sang WebView Fragment, truyền raw URL (Fragment tự ghép BASE_URL nếu cần)
                val args = bundleOf(CvWebViewFragment.ARG_CV_URL to cvRaw)
                findNavController().navigate(R.id.cvWebViewFragment, args)
            },
            onDownloadCv = { cvRaw, filename ->
                // (Tuỳ chọn) Tải luôn bằng DownloadManager nếu bạn muốn từ danh sách
                // Nếu không cần, bỏ tham số onDownloadCv khi khởi tạo adapter
                val url = if (cvRaw.startsWith("http")) cvRaw
                else ApiConfig.BASE_URL.trimEnd('/') + "/" + cvRaw.trimStart('/')

                val req = DownloadManager.Request(Uri.parse(url))
                    .setTitle(filename)
                    .setMimeType("application/pdf")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)

                val dm = requireContext().getSystemService(DownloadManager::class.java)
                dm.enqueue(req)
            }
        )
    }

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
            view_model.updateQuery(text?.toString().orEmpty())
        }

        // Collect theo vòng đời STARTED
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    view_model.studentsFiltered.collect { list ->
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
        view_model.loadAll()
    }
}

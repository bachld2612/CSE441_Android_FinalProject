package com.bachld.android.ui.doanfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.adapter.TienTrinhAdapter
import com.bachld.android.databinding.FragmentThoiGianThucHienBinding
import com.bachld.android.model.TienTrinhDoAn

class ThoiGianThucHienFragment: Fragment() {

    private var _binding: FragmentThoiGianThucHienBinding? = null
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
        _binding = FragmentThoiGianThucHienBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dữ liệu tiến trình - nội dung fix cứng, thời gian truyền động
        val tienTrinhList = listOf(
            TienTrinhDoAn(
                noiDung = "Sinh viên chủ động liên hệ với giáo viên hướng dẫn cùng hướng nghiên cứu đề tài",
                thoiGianBatDau = "01/07/2025",
                thoiGianKetThuc = "27/07/2025",
                isHighlight = true
            ),
            TienTrinhDoAn(
                noiDung = "Xét giao",
                thoiGianBatDau = "27/07/2025",
                thoiGianKetThuc = "27/07/2025"
            ),
            TienTrinhDoAn(
                noiDung = "BM phụ trách ngành phân công GVHD HPTN và thông báo tới giáo viên, sinh viên; Sinh viên liên hệ GVHD để nhận nhiệm vụ; sinh viên nộp đề cương về BM phụ trách ngành để xét duyệt",
                thoiGianBatDau = "27/07/2025",
                thoiGianKetThuc = "27/07/2025"
            ),
            TienTrinhDoAn(
                noiDung = "Bộ Môn xét duyệt đề cương và nộp về Văn phòng Khoa",
                thoiGianBatDau = "27/07/2025",
                thoiGianKetThuc = "27/07/2025"
            ),
            TienTrinhDoAn(
                noiDung = "Sinh viên thực hiện học phần tốt nghiệp",
                thoiGianBatDau = "27/07/2025",
                thoiGianKetThuc = "27/07/2025"
            ),
            TienTrinhDoAn(
                noiDung = "Chấm và bảo vệ HPTN",
                thoiGianBatDau = "27/07/2025",
                thoiGianKetThuc = "27/07/2025"
            )
        )

        val adapter = TienTrinhAdapter(tienTrinhList)
        binding.recyclerViewTienTrinh.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewTienTrinh.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
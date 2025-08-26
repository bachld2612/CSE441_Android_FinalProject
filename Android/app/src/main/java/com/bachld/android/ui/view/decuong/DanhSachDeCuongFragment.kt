package com.bachld.android.ui.view.decuong

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.databinding.FragmentDanhSachDeCuongBinding
import com.bachld.android.ui.viewmodel.DeCuongViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Color
import com.bachld.android.data.dto.response.decuong.DeCuongState

class DanhSachDeCuongFragment : Fragment() {
    private var _binding: FragmentDanhSachDeCuongBinding? = null
    private val binding get() = _binding!!

    private fun DeCuongState?.toLabel(): String = when (this) {
        DeCuongState.ACCEPTED -> "đã duyệt"
        DeCuongState.PENDING  -> "đang chờ"
        DeCuongState.CANCELED -> "bị từ chối"
        null                  -> "-"
    }

    private fun DeCuongState?.toColor(): Int = when (this) {
        DeCuongState.ACCEPTED -> Color.parseColor("#2E7D32") // xanh lá đậm
        DeCuongState.PENDING  -> Color.parseColor("#EF6C00") // cam
        DeCuongState.CANCELED -> Color.parseColor("#C62828") // đỏ
        null                  -> Color.parseColor("#1E1E1E") // mặc định
    }

    private val vm: DeCuongViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDanhSachDeCuongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.logState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.Success -> showContent(state.data)    // <-- data có thể null
                is UiState.Error   -> showEmpty()
                UiState.Idle       -> Unit
            }
        }

        vm.loadLog()
    }

    private fun showLoading() { /* optional: show progress */ }

    private fun showEmpty() = with(binding) {
        groupContent.isGone = true
        groupEmpty.isVisible = true
    }

    /** Nhận nullable để tránh lỗi type mismatch */
    private fun showContent(data: DeCuongLogResponse?) = with(binding) {
        if (data == null) { showEmpty(); return@with }

        groupEmpty.isGone = true
        groupContent.isVisible = true

        val state = data.trangThaiHienTai

        tvTrangThaiHienTai.text = state.toLabel()
        val valueColor = state?.toColor() ?: 0xFF1E1E1E.toInt()
        tvTrangThaiHienTai.setTextColor(valueColor)

        // File mới nhất (underline + click)
        val url = data.fileUrlMoiNhat
        tvFileUrl.text = if (!url.isNullOrBlank()) {
            SpannableString(extractFileName(url)).apply { setSpan(UnderlineSpan(), 0, length, 0) }
        } else {
            "—"
        }
        tvFileUrl.setTextColor(0xFF1565C0.toInt())
        tvFileUrl.paintFlags = tvFileUrl.paintFlags or Paint.ANTI_ALIAS_FLAG
        tvFileUrl.isClickable = !url.isNullOrBlank()
        tvFileUrl.setOnClickListener { url?.let { safeOpenUrl(it) } }

        // Ngày nộp gần nhất
        tvNgayNopGanNhat.text = "Ngày nộp: ${formatDate(data.ngayNopGanNhat)}"

        // Tổng số lần nộp
        tvTongSoLanNop.text = "Số lần nộp: ${(data.tongSoLanNop ?: 0)}"

        // Danh sách lý do từ chối
        containerReasons.removeAllViews()
        val notes = data.cacNhanXetTuChoi.orEmpty()
        notes.forEachIndexed { index, note ->
            val tv = TextView(requireContext()).apply {
                val idx = index + 1
                text = "Lý do từ chối lần $idx: ${note.lyDo ?: "(Không có lý do)"}"
                textSize = 14f
                setTextColor(0xFF1E1E1E.toInt())
                setPadding(0, if (index == 0) 0 else dp(4), 0, 0)
            }
            containerReasons.addView(tv)
        }
    }

    // ----- helpers -----
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun formatDate(s: String?): String = try {
        if (s.isNullOrBlank()) "-" else {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val out = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            out.format(parser.parse(s)!!)
        }
    } catch (_: Exception) { s ?: "-" }

    private fun extractFileName(url: String): String =
        url.trim().substringAfterLast('/').ifBlank { url }

    private fun safeOpenUrl(url: String) {
        try {
            val target = if (
                url.startsWith("http", true) ||
                url.startsWith("content:", true) ||
                url.startsWith("file:", true)
            ) Uri.parse(url) else Uri.parse("file://$url")
            startActivity(Intent(Intent.ACTION_VIEW, target))
        } catch (_: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(url), "text/html")
                })
            } catch (_: Exception) { /* ignore */ }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

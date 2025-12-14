package com.bachld.android.ui.view.hoidong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.core.UiState
import com.bachld.android.databinding.FragmentChiTietHoiDongBinding
import com.bachld.android.ui.adapter.ChiTietHoiDongAdapter
import com.bachld.android.viewmodel.HoiDongDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ChiTietHoiDongFragment : Fragment() {

    private var _vb: FragmentChiTietHoiDongBinding? = null
    private val vb get() = _vb!!

    private val vm: HoiDongDetailViewModel by viewModels()
    private val svAdapter = ChiTietHoiDongAdapter()

    // Dùng java.text để không cần desugaring
    private val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outFmt = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _vb = FragmentChiTietHoiDongBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hoiDongId  = requireArguments().getLong("hoiDongId")
        val tenHoiDong = requireArguments().getString("tenHoiDong") ?: "Hội đồng"

        vb.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        vb.rvStudents.adapter = svAdapter

        vm.load(hoiDongId)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is UiState.Success -> {
                        val d = st.data

                        // 1) Tên hội đồng
                        vb.tvCommitteeName.text = d.tenHoiDong

                        // 2) Ngày bảo vệ (ngắn gọn, ít rớt dòng)
                        fun fmt(s: String) = runCatching { outFmt.format(inFmt.parse(s)!!) }.getOrElse { s }
                        vb.tvDateDetail.text = "${fmt(d.thoiGianBatDau)}–${fmt(d.thoiGianKetThuc)}"

                        // 3) Ẩn/hiện theo loại hội đồng
                        when (d.loaiHoiDong) {
                            "PEER_REVIEW" -> {
                                vb.rowChair.isVisible = !d.chuTich.isNullOrBlank()
                                vb.tvChairDetail.text = d.chuTich ?: "-"

                                vb.rowSecretary.isGone = true
                                vb.rowPB1.isGone = true
                                vb.rowPB2.isGone = true
                                vb.rowPB3.isGone = true
                            }

                            "DEFENSE" -> {
                                // Chủ tịch
                                vb.rowChair.isVisible = !d.chuTich.isNullOrBlank()
                                vb.tvChairDetail.text = d.chuTich ?: "-"

                                // Thư ký
                                vb.rowSecretary.isVisible = !d.thuKy.isNullOrBlank()
                                vb.tvSecretaryDetail.text = d.thuKy ?: "-"

                                // Phản biện 1..n
                                val pbs = d.giangVienPhanBien ?: emptyList()
                                fun setPB(row: View, tv: TextView, idx: Int) {
                                    if (idx < pbs.size && !pbs[idx].isNullOrBlank()) {
                                        row.isVisible = true
                                        tv.text = pbs[idx]
                                    } else row.isGone = true
                                }
                                setPB(vb.rowPB1, vb.tvReviewer1Detail, 0)
                                setPB(vb.rowPB2, vb.tvReviewer2Detail, 1)
                                setPB(vb.rowPB3, vb.tvReviewer3Detail, 2)
                            }

                            else -> {
                                // Mặc định: hiển thị có gì
                                vb.rowChair.isVisible = !d.chuTich.isNullOrBlank()
                                vb.tvChairDetail.text = d.chuTich ?: "-"

                                vb.rowSecretary.isVisible = !d.thuKy.isNullOrBlank()
                                vb.tvSecretaryDetail.text = d.thuKy ?: "-"

                                val pbs = d.giangVienPhanBien ?: emptyList()
                                vb.rowPB1.isVisible = pbs.getOrNull(0)?.isNotBlank() == true
                                vb.rowPB2.isVisible = pbs.getOrNull(1)?.isNotBlank() == true
                                vb.rowPB3.isVisible = pbs.getOrNull(2)?.isNotBlank() == true
                                vb.tvReviewer1Detail.text = pbs.getOrNull(0) ?: "-"
                                vb.tvReviewer2Detail.text = pbs.getOrNull(1) ?: "-"
                                vb.tvReviewer3Detail.text = pbs.getOrNull(2) ?: "-"
                            }
                        }

                        // 4) Danh sách sinh viên
                        svAdapter.submit(d.sinhVienList)
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), st.message ?: "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() { _vb = null; super.onDestroyView() }
}
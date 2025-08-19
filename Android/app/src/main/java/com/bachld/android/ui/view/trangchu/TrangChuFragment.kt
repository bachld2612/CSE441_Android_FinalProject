package com.bachld.android.ui.view.trangchu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.databinding.FragmentTrangChuScrollingBinding
import com.bachld.android.ui.adapter.ThongBaoAdapter
import com.bachld.android.ui.view.thongbao.ThongBaoDetailFragment
import com.bachld.android.ui.viewmodel.ThongBaoViewModel
@RequiresApi(Build.VERSION_CODES.O)
class TrangChuFragment : Fragment(R.layout.fragment_trang_chu_scrolling) {

    private var _binding: FragmentTrangChuScrollingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ThongBaoViewModel by viewModels()
    private lateinit var adapter: ThongBaoAdapter
    

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrangChuScrollingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
        observeDetailState()

        // Gọi API lấy thông báo list
        viewModel.fetchThongBao()
    }

    private fun setupRecyclerView() {
        adapter = ThongBaoAdapter { thongBao ->
            // Khi click vào item → gọi API detail
            viewModel.fetchThongBaoDetail(thongBao.id)
        }
        binding.recyclerViewNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TrangChuFragment.adapter
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> { }
                is UiState.Loading -> {
                    Toast.makeText(requireContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show()
                }
                is UiState.Success -> {
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message ?: "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeDetailState() {
        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    val detail = state.data
                    val action = TrangChuFragmentDirections
                        .actionTrangChuToThongBaoDetail(
                            id = detail.id,
                            title = detail.tieuDe,
                            content = detail.noiDung,
                            fileUrl = detail.fileUrl,
                            createdAt = detail.createdAt.toString()
                        )
                    findNavController().navigate(action)

                    // 🔑 Quan trọng: reset để khi quay lại không tự navigate nữa
                    viewModel.clearDetailState()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message ?: "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show()
                }
                else -> { /* Idle/Loading: bỏ qua */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

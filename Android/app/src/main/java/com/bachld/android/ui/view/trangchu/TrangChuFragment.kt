package com.bachld.android.ui.view.trangchu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.core.UserPrefs
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

        val role = UserPrefs(requireContext()).getCached()?.role?.lowercase()
        val isGV = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"
        if(isGV){
            binding.layoutTopState.visibility = View.GONE
            binding.tvThongBao.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = 20
            }
        }else{
            binding.layoutTopState.visibility = View.VISIBLE
        }

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
                    val role = UserPrefs(requireContext()).getCached()?.role?.lowercase()
                    val isGV = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"

                    // Dùng Bundle để tái sử dụng cùng một set args
                    val args = Bundle().apply {
                        putLong("id", detail.id)
                        putString("title", detail.tieuDe)
                        putString("content", detail.noiDung)
                        putString("fileUrl", detail.fileUrl)
                        putString("createdAt", detail.createdAt.toString())
                    }

                    val actionId = if (isGV)
                        R.id.action_gv_trang_chu_to_thong_bao_detail
                    else
                        R.id.action_trang_chu_to_thong_bao_detail

                    findNavController().navigate(actionId, args)

                    viewModel.clearDetailState()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message ?: "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

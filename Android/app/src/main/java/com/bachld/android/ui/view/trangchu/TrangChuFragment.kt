package com.bachld.android.ui.view.trangchu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.impl.DeTaiRepositoryImpl
import com.bachld.android.databinding.FragmentTrangChuScrollingBinding
import com.bachld.android.ui.adapter.ThongBaoAdapter
import com.bachld.android.ui.viewmodel.DoAnDetailViewModel
import com.bachld.android.ui.viewmodel.ThongBaoViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class TrangChuFragment : Fragment(R.layout.fragment_trang_chu_scrolling) {

    private var _binding: FragmentTrangChuScrollingBinding? = null
    private val binding get() = _binding!!

    private val tbViewModel: ThongBaoViewModel by viewModels()
    private lateinit var adapter: ThongBaoAdapter

    private val doAnVm: DoAnDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DeTaiRepositoryImpl(ApiClient.deTaiApi, UserPrefs(requireContext()))
                return DoAnDetailViewModel(repo) as T
            }
        }
    }

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

        if (isGV) {
            binding.layoutTopState.visibility = View.GONE
            binding.tvThongBao.updateLayoutParams <ViewGroup.MarginLayoutParams> {
                topMargin = 5
            }
        } else {
            binding.layoutTopState.visibility = View.GONE

            binding.btnDeNghiHoan.setOnClickListener {
                findNavController().navigate(R.id.action_trang_chu_to_hoan_do_an)
            }
            binding.btnDangKyDeTai.setOnClickListener {
                findNavController().navigate(R.id.action_trang_chu_to_dang_ky_do_an)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        doAnVm.project.collect { p ->
                            binding.layoutTopState.visibility = if (p == null || p.status != "ACCEPTED") View.VISIBLE else View.GONE
                        }
                    }
                    launch {
                        doAnVm.error.collect { msg ->
                            if (!msg.isNullOrBlank()) {
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                                binding.layoutTopState.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

            doAnVm.load(forceRefresh = true)
        }

        setupRecyclerView()
        observeUiState()
        observeDetailState()

        tbViewModel.fetchThongBao()
    }

    private fun setupRecyclerView() {
        adapter = ThongBaoAdapter { thongBao ->
            tbViewModel.fetchThongBaoDetail(thongBao.id)
        }
        binding.recyclerViewNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TrangChuFragment.adapter
        }
    }

    private fun observeUiState() {
        tbViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    val list = state.data
                    if(list.isEmpty()) {
                        binding.recyclerViewNotifications.visibility = View.GONE
                        binding.layoutChuaDangKy.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewNotifications.visibility = View.VISIBLE
                        binding.layoutChuaDangKy.visibility = View.GONE
                    }
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message ?: "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeDetailState() {
        tbViewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    val detail = state.data
                    val role = UserPrefs(requireContext()).getCached()?.role?.lowercase()
                    val isGV = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"

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

                    tbViewModel.clearDetailState()
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

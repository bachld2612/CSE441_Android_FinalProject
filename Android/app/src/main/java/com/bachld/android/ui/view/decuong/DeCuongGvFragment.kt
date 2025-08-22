package com.bachld.android.ui.view.decuong

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.databinding.FragmentDeCuongListBinding
import com.bachld.android.ui.adapter.DeCuongGvAdapter
import com.bachld.android.ui.viewmodel.DeCuongGvViewModel
import kotlinx.coroutines.launch

class DeCuongGvFragment : Fragment(R.layout.fragment_de_cuong_list) {

    private var _binding: FragmentDeCuongListBinding? = null
    private val binding get() = _binding!!

    private val vm: DeCuongGvViewModel by viewModels()
    private lateinit var adapter: DeCuongGvAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDeCuongListBinding.bind(view)

        adapter = DeCuongGvAdapter(
            onApprove = { id -> vm.approve(id) },
            onReject  = { id, reason -> vm.reject(id, reason) }
        )
        binding.rvDeCuong.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeCuong.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Danh sách
                launch {
                    vm.listState.collect { st ->
                        when (st) {
                            is UiState.Loading -> { /* show loading nếu cần */ }
                            is UiState.Error   -> toast(st.message ?: "Lỗi tải danh sách")
                            is UiState.Success -> {
                                val list = st.data.result?.content.orEmpty()
                                adapter.submitList(list)
                            }
                            else -> Unit
                        }
                    }
                }

                // Hành động duyệt / từ chối
                launch {
                    vm.actionState.collect { st ->
                        when (st) {
                            is UiState.Loading -> Unit
                            is UiState.Error   -> { toast(st.message ?: "Thao tác thất bại"); vm.clearAction() }
                            is UiState.Success -> {
                                val status = st.data.result?.status?.uppercase()
                                val msg = st.data.message
                                    ?: when (status) {
                                        "ACCEPTED" -> "Đã duyệt đề cương"
                                        "CANCELLED", "CENCELLED" -> "Đã từ chối đề cương"
                                        else -> "Thành công"
                                    }
                                toast(msg)
                                vm.load(0, 10)
                                vm.clearAction()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }

        vm.load(0, 10)
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

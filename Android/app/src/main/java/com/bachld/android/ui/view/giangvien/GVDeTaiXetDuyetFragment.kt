package com.bachld.android.ui.view.giangvien

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
import com.bachld.android.databinding.FragmentXetDuyetDetaiListBinding
import com.bachld.android.ui.adapter.XetDuyetDeTaiAdapter
import com.bachld.android.ui.viewmodel.GVXetDuyetViewModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class GVDeTaiXetDuyetFragment : Fragment(R.layout.fragment_xet_duyet_detai_list) {

    private var _binding: FragmentXetDuyetDetaiListBinding? = null
    private val binding get() = _binding!!

    private val vm: GVXetDuyetViewModel by viewModels()
    private lateinit var adapter: XetDuyetDeTaiAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentXetDuyetDetaiListBinding.bind(view)

        adapter = XetDuyetDeTaiAdapter(
            onApprove = { id -> vm.approve(id) },
            onReject  = { id, reason -> vm.reject(id, reason) }
        )
        binding.rvXetDuyet.layoutManager = LinearLayoutManager(requireContext())
        binding.rvXetDuyet.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    vm.listState.collect { st ->
                        when (st) {
                            is UiState.Loading -> {  }
                            is UiState.Error -> toast(st.message ?: "Lỗi tải danh sách")
                            is UiState.Success -> {

                                val code = st.data.code
                                when (code) {
                                    1110 -> {
                                        toast("Bạn không phải là giảng viên hướng dẫn của đề tài này")
                                    }
                                    1109 -> {
                                        toast("Trạng thái đề tài không hợp lệ")
                                    }
                                    1111 -> {
                                        toast("Chỉ được duyệt đề tài trong trạng thái Chờ duyệt")
                                    }
                                    1000 -> {
                                        val page = st.data.result
                                        val list = page?.content.orEmpty()
                                        if(list.isEmpty()) {
                                            binding.rvXetDuyet.visibility = View.GONE
                                            binding.layoutChuaDangKy.visibility = View.VISIBLE
                                        } else {
                                            binding.rvXetDuyet.visibility = View.VISIBLE
                                            binding.layoutChuaDangKy.visibility = View.GONE
                                        }
                                        adapter.submitList(list)
                                    }
                                    else -> {
                                        toast("Lỗi tải danh sách")
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    vm.actionState.collect { st ->
                        when (st) {
                            is UiState.Loading -> Unit
                            is UiState.Error   -> {
                                toast(st.message ?: "Thao tác thất bại")
                                vm.clearAction()
                            }
                            is UiState.Success -> {
                                val res = st.data
                                val status = res.result?.status?.uppercase()
                                val msg = res.message
                                    ?: when (status) {
                                        "ACCEPTED" -> "Đã duyệt đề tài"
                                        "CANCELLED", "CENCELLED" -> "Đã từ chối đề tài"
                                        else -> "Thành công"
                                    }
                                toast(msg)
                                vm.load(page = 0, size = 10)
                                vm.clearAction()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }

        vm.load(page = 0, size = 10)
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

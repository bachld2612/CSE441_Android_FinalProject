package com.bachld.android.ui.view.decuong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.databinding.FragmentNopDeCuongBinding
import com.bachld.android.ui.viewmodel.DeCuongViewModel

class NopDeCuongFragment : Fragment() {

    private var _binding: FragmentNopDeCuongBinding? = null
    private val binding get() = _binding!!

    private val vm: DeCuongViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNopDeCuongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLanNopValue.text = "-"
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Nộp đề cương"

        // Chỉ bật nút khi người dùng có nhập gì đó; validate chi tiết để BE xử lý
        binding.etUrl.doOnTextChanged { text, _, _, _ ->
            binding.btnSubmit.isEnabled = !text.isNullOrBlank()
            binding.tilUrl.error = null   // không hiển thị lỗi client-side ở đây
        }

        // Nhấn submit: gửi thẳng lên backend
        binding.btnSubmit.setOnClickListener {
            val url = binding.etUrl.text?.toString()?.trim().orEmpty()
            vm.submit(url)
        }

        // Quan sát log
        vm.logState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> state.data?.let { bindLog(it) }
                is UiState.Error   -> toast(state.message ?: "Không tải được log")
                else -> Unit
            }
        }

        // Quan sát submit
        vm.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.text = "Đang nộp…"
                }
                is UiState.Success -> {
                    binding.btnSubmit.text = "Nộp đề cương"
                    toast("Nộp thành công!")
                    binding.etUrl.setText("")
                    binding.btnSubmit.isEnabled = false   // chờ user nhập URL mới
                    vm.resetSubmitState()
                }
                is UiState.Error -> {
                    binding.btnSubmit.text = "Nộp đề cương"
                    binding.btnSubmit.isEnabled = true
                    toast(state.message ?: "Lỗi khi nộp") // 👈 đúng message do BE trả
                    vm.resetSubmitState()
                }
                else -> Unit
            }
        }

        // Lần đầu tải log
        vm.loadLog()
    }

    private fun bindLog(data: DeCuongLogResponse) {
        binding.tvLanNopValue.text = (data.tongSoLanNop ?: 0).toString()
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

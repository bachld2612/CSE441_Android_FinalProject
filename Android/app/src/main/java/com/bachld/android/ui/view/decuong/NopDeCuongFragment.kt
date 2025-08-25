// app/src/main/java/com/bachld/android/ui/view/decuong/NopDeCuongFragment.kt
package com.bachld.android.ui.view.decuong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongState
import com.bachld.android.databinding.FragmentNopDeCuongBinding
import com.bachld.android.ui.viewmodel.DeCuongViewModel
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import java.util.Locale

class NopDeCuongFragment : Fragment() {

    private var _binding: FragmentNopDeCuongBinding? = null
    private val binding get() = _binding!!

    private val vm: DeCuongViewModel by activityViewModels()

    // TEXT INLINE
    private val TXT_URL_ONLY_DRIVE = "Chỉ chấp nhận liên kết Google Drive (drive.google.com)."
    private val TXT_SUBMITTING = "Đang nộp…"
    private val TXT_SUBMIT = "Nộp đề cương"
    private val TXT_SUBMIT_SUCCESS = "Nộp thành công!"
    private val TXT_SUBMIT_ERROR = "Lỗi khi nộp"
    private val TXT_LOG_ERROR = "Không tải được log"
    private val TXT_ACCEPTED_ERROR_ON_CLICK = "Đề cương đã được duyệt. Không thể nộp thêm."

    // Chỉ chấp nhận host Drive
    private val allowedHosts = setOf("drive.google.com")

    private var isUrlValid = false
    private var isAccepted = false

    private fun isGoogleDriveUrl(normalized: String): Boolean {
        val uri = runCatching { Uri.parse(normalized) }.getOrNull() ?: return false
        val schemeOk = uri.scheme.equals("http", true) || uri.scheme.equals("https", true)
        if (!schemeOk) return false
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false
        return host in allowedHosts
    }

    private fun validateUrlAndShowError(raw: String?): Boolean {
        val s = raw?.trim().orEmpty()
        if (s.isEmpty()) { binding.tilUrl.error = null; return false }
        val normalized = if (s.startsWith("www.", ignoreCase = true)) "https://$s" else s
        val valid = isGoogleDriveUrl(normalized)
        if (valid) {
            binding.tilUrl.error = null
            if (normalized != s) binding.etUrl.setText(normalized)
        } else {
            binding.tilUrl.error = TXT_URL_ONLY_DRIVE
        }
        return valid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNopDeCuongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLanNopValue.text = "-"
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = TXT_SUBMIT

        // Validate khi gõ URL
        binding.etUrl.doOnTextChanged { text, _, _, _ ->
            isUrlValid = validateUrlAndShowError(text?.toString())
            updateSubmitButtonEnabled()
        }

        // Quan sát log
        vm.logState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> Unit
                is UiState.Success -> state.data?.let { bindLog(it) }
                is UiState.Error ->
                    Snackbar.make(binding.root, state.message ?: TXT_LOG_ERROR, Snackbar.LENGTH_LONG).show()
                UiState.Idle -> Unit
            }
        }

        // Cập nhật cờ isAccepted nhưng KHÔNG disable UI — để khi bấm sẽ báo lỗi
        vm.currentState.observe(viewLifecycleOwner) { st ->
            isAccepted = (st == DeCuongState.ACCEPTED)
            // không khóa input/nút ở đây
            updateSubmitButtonEnabled()
        }

        // Submit
        binding.btnSubmit.setOnClickListener {
            // Nếu đã duyệt → báo lỗi NGAY khi click, không gọi ViewModel.submit
            if (isAccepted) {
                Snackbar.make(binding.root, TXT_ACCEPTED_ERROR_ON_CLICK, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val url = binding.etUrl.text?.toString()?.trim().orEmpty()
            if (!validateUrlAndShowError(url)) {
                updateSubmitButtonEnabled()
                return@setOnClickListener
            }
            vm.submit(url)
        }

        // Quan sát submit state
        vm.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.text = TXT_SUBMITTING
                }
                is UiState.Success -> {
                    binding.btnSubmit.text = TXT_SUBMIT
                    Snackbar.make(binding.root, TXT_SUBMIT_SUCCESS, Snackbar.LENGTH_LONG).show()
                    binding.etUrl.setText("")
                    updateSubmitButtonEnabled()
                    vm.resetSubmitState()
                }
                is UiState.Error -> {
                    binding.btnSubmit.text = TXT_SUBMIT
                    updateSubmitButtonEnabled()
//                    Snackbar.make(binding.root, state.message ?: TXT_SUBMIT_ERROR, Snackbar.LENGTH_LONG).show()
                    if(state.message == "1212") {
                        Snackbar.make(binding.root,"Đề tài chưa được duyệt" , Snackbar.LENGTH_LONG).show()
                    }else if(state.message == "1216"){
                        Snackbar.make(binding.root,"Chưa tới thời gian nộp đề cương" , Snackbar.LENGTH_LONG).show()
                    }else if (state.message == "1215"){
                        Snackbar.make(binding.root,"Ngoài thời gian nộp đề cương" , Snackbar.LENGTH_LONG).show()
                    }else if (state.message == "1202"){
                        Snackbar.make(binding.root,"Url không được để trống" , Snackbar.LENGTH_LONG).show()
                    }else if( state.message == "1206") {
                        Snackbar.make(binding.root, "Đề cương đã được duyệt", Snackbar.LENGTH_LONG).show()
                    }
                    vm.resetSubmitState()
                }
                UiState.Idle -> Unit
            }
        }

        // Lần đầu tải log (từ đó ViewModel suy ra trạng thái)
        vm.loadLog()
    }

    private fun bindLog(data: DeCuongLogResponse) {
        binding.tvLanNopValue.text = (data.tongSoLanNop ?: 0).toString()
    }

    private fun updateSubmitButtonEnabled() {
        // vẫn chỉ bật khi URL hợp lệ (để tránh spam), KHÔNG phụ thuộc isAccepted
        binding.btnSubmit.isEnabled = isUrlValid
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
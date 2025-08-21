package com.bachld.android.ui.view.decuong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.databinding.FragmentNopDeCuongBinding
import com.bachld.android.ui.viewmodel.DeCuongViewModel
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import java.util.Locale

class NopDeCuongFragment : Fragment() {

    private var _binding: FragmentNopDeCuongBinding? = null
    private val binding get() = _binding!!

    private val vm: DeCuongViewModel by activityViewModels()

    // Chỉ chấp nhận host Drive/Docs
    private val allowedHosts = setOf("drive.google.com")

    private var isUrlValid: Boolean = false

    private fun isGoogleDriveUrl(normalized: String): Boolean {
        val uri = runCatching { Uri.parse(normalized) }.getOrNull() ?: return false
        val schemeOk = uri.scheme.equals("http", true) || uri.scheme.equals("https", true)
        if (!schemeOk) return false
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false
        return host in allowedHosts
    }

    private fun validateUrlAndShowError(raw: String?): Boolean {
        val s = raw?.trim().orEmpty()

        // Trống → xoá lỗi, nhưng không coi là hợp lệ
        if (s.isEmpty()) {
            binding.tilUrl.error = null
            return false
        }

        // Tự chèn https:// nếu người dùng gõ "www."
        val normalized = if (s.startsWith("www.", ignoreCase = true)) "https://$s" else s

        val valid = isGoogleDriveUrl(normalized)

        if (valid) {
            binding.tilUrl.error = null
            if (normalized != s) binding.etUrl.setText(normalized)
        } else {
            binding.tilUrl.error = "Chỉ chấp nhận liên kết Google Drive (drive.google.com)."
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
                    Snackbar.make(binding.root, state.message ?: "Không tải được log", Snackbar.LENGTH_LONG).show()
                UiState.Idle -> Unit
            }
        }

        // Quan sát submit
        vm.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.text = getString(R.string.btn_submitting)
                }
                is UiState.Success -> {
                    binding.btnSubmit.text = getString(R.string.btn_submit_decuong)
                    Snackbar.make(binding.root, "Nộp thành công!", Snackbar.LENGTH_LONG).show()
                    binding.etUrl.setText("")
                    updateSubmitButtonEnabled()
                    vm.resetSubmitState()
                }
                is UiState.Error -> {
                    binding.btnSubmit.text = getString(R.string.btn_submit_decuong)
                    updateSubmitButtonEnabled()
                    Snackbar.make(binding.root, state.message ?: "Lỗi khi nộp", Snackbar.LENGTH_LONG).show()
                    vm.resetSubmitState()
                }
                UiState.Idle -> Unit
            }
        }

        // Lần đầu tải log
        vm.loadLog()

        // Nút Submit
        binding.btnSubmit.setOnClickListener {
            val url = binding.etUrl.text?.toString()?.trim().orEmpty()
            if (!validateUrlAndShowError(url)) {
                updateSubmitButtonEnabled()
                return@setOnClickListener
            }
            vm.submit(url)
        }
    }

    private fun bindLog(data: DeCuongLogResponse) {
        binding.tvLanNopValue.text = (data.tongSoLanNop ?: 0).toString()
        // (tuỳ chọn) hiển thị thêm: data.fileUrlMoiNhat, data.ngayNopGanNhat ...
    }

    private fun updateSubmitButtonEnabled() {
        binding.btnSubmit.isEnabled = isUrlValid
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

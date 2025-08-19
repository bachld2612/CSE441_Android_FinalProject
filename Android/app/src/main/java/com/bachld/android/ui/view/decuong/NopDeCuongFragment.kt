package com.bachld.android.ui.view.decuong

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.databinding.FragmentNopDeCuongBinding
import com.bachld.android.ui.viewmodel.DeCuongViewModel
import com.bachld.android.ui.viewmodel.SharedDeTaiViewModel
import com.google.android.material.snackbar.Snackbar

class NopDeCuongFragment : Fragment() {

    private var _binding: FragmentNopDeCuongBinding? = null
    private val binding get() = _binding!!

    private val vm: DeCuongViewModel by activityViewModels()

    // Lấy deTaiId từ Shared VM (đã được set tại ThongTinDoAnFragment)
    private val shared by activityViewModels<SharedDeTaiViewModel>()
    private var deTaiId: Long? = null

    private var pickedUri: Uri? = null

    // SAF: chọn file bất kỳ
    private val pickDocLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedUri = uri
            binding.tvPickedFile.visibility = View.VISIBLE
            binding.tvPickedFile.text = uri.lastPathSegment ?: uri.toString()
        }
        updateSubmitButtonEnabled()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNopDeCuongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLanNopValue.text = "-"
        binding.btnSubmit.isEnabled = false

        binding.pickZone.setOnClickListener { pickDocLauncher.launch(arrayOf("*/*")) }
        binding.etUrl.doOnTextChanged { _, _, _, _ -> updateSubmitButtonEnabled() }

        // 1) Nhận id từ Shared VM
        shared.deTaiId.observe(viewLifecycleOwner) { id ->
            deTaiId = id
            // 2) (tùy chọn) gọi loadLog() để cập nhật lần nộp; /sv/log không cần id
            vm.loadLog()
        }

        // 3) Quan sát state
        vm.logState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> Unit
                is UiState.Success -> state.data?.let { bindLog(it) }
                is UiState.Error ->
                    Snackbar.make(binding.root, state.message ?: "Không tải được log", Snackbar.LENGTH_LONG).show()
                UiState.Idle -> Unit
            }
        }

        vm.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.text = getString(R.string.btn_submitting)
                }
                is UiState.Success -> {
                    binding.btnSubmit.text = getString(R.string.btn_submit_decuong)
                    Snackbar.make(binding.root, "Nộp thành công!", Snackbar.LENGTH_LONG).show()
                    pickedUri = null
                    binding.tvPickedFile.visibility = View.GONE
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

        // 4) Submit
        binding.btnSubmit.setOnClickListener {
            val id = deTaiId
            if (id == null || id <= 0L) {
                Snackbar.make(
                    binding.root,
                    "Chưa xác định được mã đề tài. Hãy mở mục 'Thông tin đồ án' trước khi nộp.",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            vm.submit(
                deTaiId = id,
                fileUri = pickedUri,
                fileUrl = binding.etUrl.text?.toString()
            )
        }
    }

    private fun bindLog(data: DeCuongLogResponse) {
        binding.tvLanNopValue.text = (data.tongSoLanNop ?: 0).toString()
        // Có thể hiển thị thêm: data.fileUrlMoiNhat, data.ngayNopGanNhat...
    }

    private fun updateSubmitButtonEnabled() {
        val hasFile = pickedUri != null
        val hasUrl = !binding.etUrl.text.isNullOrBlank()
        binding.btnSubmit.isEnabled = hasFile || hasUrl
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

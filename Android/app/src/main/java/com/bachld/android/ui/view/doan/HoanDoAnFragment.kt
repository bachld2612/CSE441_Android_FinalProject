package com.bachld.android.ui.view.doan

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.databinding.FragmentHoanDoAnBinding
import com.bachld.android.ui.viewmodel.HoanDoAnViewModel
import com.google.android.material.snackbar.Snackbar

class HoanDoAnFragment : Fragment() {

    private var _binding: FragmentHoanDoAnBinding? = null
    private val binding get() = _binding!!

    private val vm: HoanDoAnViewModel by viewModels()

    private var minhChungUri: Uri? = null
    private var lyDoValid = false

    // SAF: chỉ chọn PDF
    private val pickFile = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            if (!isPdf(uri)) {
                Snackbar.make(binding.root, "Chỉ chấp nhận file PDF (.pdf).", Snackbar.LENGTH_LONG).show()
                return@registerForActivityResult
            }
            // giữ quyền đọc lâu dài
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { /* ignore nếu không persist được */ }

            minhChungUri = uri
            binding.tvKeoThaTaiDay.text = getFileName(uri) ?: uri.lastPathSegment ?: uri.toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHoanDoAnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Validate lý do
        binding.edtLyDoHoan.doOnTextChanged { text, _, _, _ ->
            lyDoValid = !text.isNullOrBlank()
            binding.edtLyDoHoan.error = if (lyDoValid) null else getString(R.string.error_please_input_reason)
            updateButtonState()
        }

        // Chọn file PDF
        binding.frameUploadMinhChung.setOnClickListener {
            try {
                pickFile.launch(arrayOf("application/pdf"))
            } catch (_: ActivityNotFoundException) {
                Snackbar.make(binding.root, getString(R.string.error_no_file_picker), Snackbar.LENGTH_LONG).show()
            }
        }

        // Quan sát state
        vm.submitState.observe(viewLifecycleOwner) { st ->
            when (st) {
                is UiState.Loading -> {
                    binding.btnGuiDeNghi.isEnabled = false
                    binding.btnGuiDeNghi.text = getString(R.string.sending)
                }
                is UiState.Success -> {
                    binding.btnGuiDeNghi.text = getString(R.string.gui_de_nghi)
                    Snackbar.make(binding.root, getString(R.string.submit_success), Snackbar.LENGTH_LONG).show()
                    // dọn form
                    binding.edtLyDoHoan.setText("")
                    minhChungUri = null
                    binding.tvKeoThaTaiDay.setText(R.string.chon_file_de_upload)
                    vm.reset()
                }
                is UiState.Error -> {
                    binding.btnGuiDeNghi.text = getString(R.string.gui_de_nghi)
                    Snackbar.make(binding.root, st.message ?: getString(R.string.submit_failed), Snackbar.LENGTH_LONG).show()
                    updateButtonState()
                    vm.reset()
                }
                UiState.Idle -> Unit
            }
        }

        // Nút gửi → popup xác nhận
        binding.btnGuiDeNghi.setOnClickListener {
            val lyDo = binding.edtLyDoHoan.text?.toString()?.trim().orEmpty()
            if (lyDo.isBlank()) {
                binding.edtLyDoHoan.error = getString(R.string.error_please_input_reason)
                return@setOnClickListener
            }
            // Nếu có file thì bắt buộc phải là PDF
            if (minhChungUri != null && !isPdf(minhChungUri!!)) {
                Snackbar.make(binding.root, getString(R.string.only_pdf_allowed), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.confirm_postpone_dialog))
                .setPositiveButton(R.string.confirm) { _, _ ->
                    vm.submit(lyDo, minhChungUri)
                }
                .setNegativeButton(R.string.back, null)
                .show()
        }

        updateButtonState()
    }

    private fun updateButtonState() {
        binding.btnGuiDeNghi.isEnabled = lyDoValid
    }

    private fun isPdf(uri: Uri): Boolean {
        val cr = requireContext().contentResolver
        val mime = cr.getType(uri)
        if ("application/pdf".equals(mime, ignoreCase = true)) return true
        // fallback: kiểm tra đuôi .pdf theo tên hiển thị
        val name = getFileName(uri)?.lowercase()
        return name?.endsWith(".pdf") == true
    }

    private fun getFileName(uri: Uri): String? {
        // ưu tiên DocumentFile
        DocumentFile.fromSingleUri(requireContext(), uri)?.name?.let { return it }
        // fallback query OpenableColumns
        return runCatching {
            requireContext().contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
        }.getOrNull()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

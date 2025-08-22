package com.bachld.android.ui.view.doan

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.databinding.FragmentDangKyDoAnBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class DangKyDoAnFragment : Fragment() {

    private var _vb: FragmentDangKyDoAnBinding? = null
    private val vb get() = _vb!!

    private var selectedGvId: Long? = null
    private var filePart: MultipartBody.Part? = null

    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val name = queryDisplayName(uri) ?: "upload.bin"
            vb.tvKeoThaTaiDay.text = name
            filePart = uriToPart(uri, "fileTongQuan", name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _vb = FragmentDangKyDoAnBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Tải danh sách giảng viên cho dropdown
        loadGiangVienDropdown()

        // Chọn file
        vb.frameUploadFile.setOnClickListener { pickFile.launch("*/*") }

        // Gửi đăng ký
        vb.btnGuiDangKy.setOnClickListener { submit() }
    }

    private fun loadGiangVienDropdown() {
        lifecycleScope.launch {
            try {
                // /api/v1/giang-vien/list?size=200
                val page = ApiClient.giangVienApi.listGiangVien(size = 200).result
                val list = page?.content ?: emptyList()

                val names = list.map { it.hoTen }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                vb.actGvhd.setAdapter(adapter)

                vb.actGvhd.setOnItemClickListener { _, _, pos, _ ->
                    selectedGvId = list[pos].id
                    vb.tilGvhd.error = null
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Không tải được danh sách GVHD", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun submit() {
        val gvId = selectedGvId
        val ten = vb.edtTenDeTai.text?.toString()?.trim().orEmpty()

        if (gvId == null) {
            vb.tilGvhd.error = "Chọn GVHD"
            vb.actGvhd.requestFocus()
            return
        }
        if (ten.isBlank()) {
            vb.edtTenDeTai.error = "Nhập tên đề tài"
            vb.edtTenDeTai.requestFocus()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                val gvRB  = gvId.toString().toRequestBody("text/plain".toMediaType())
                val tenRB = ten.toRequestBody("text/plain".toMediaType())

                val res = ApiClient.deTaiApi.registerDeTai(
                    gvhdId = gvRB,
                    tenDeTai = tenRB,
                    fileTongQuan = filePart
                ).result

                Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                setFragmentResult("dangKyDeTai", Bundle().apply { putBoolean("changed", true) })
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Lỗi đăng ký", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        vb.btnGuiDangKy.isEnabled = !loading
        vb.btnGuiDangKy.text = if (loading) "Đang gửi..." else "Gửi đăng ký"
        vb.imgUpload.isVisible = !loading
    }

    private fun queryDisplayName(uri: Uri): String? {
        var name: String? = null
        val cr = requireContext().contentResolver
        val c: Cursor? = cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        c?.use { if (it.moveToFirst()) name = it.getString(0) }
        return name
    }

    private fun uriToPart(uri: Uri, partName: String, filename: String): MultipartBody.Part? {
        val cr = requireContext().contentResolver
        val mime = cr.getType(uri) ?: "application/octet-stream"
        val input: InputStream = cr.openInputStream(uri) ?: return null
        val bytes = input.readBytes()
        val body = bytes.toRequestBody(mime.toMediaType())
        return MultipartBody.Part.createFormData(partName, filename, body)
    }

    override fun onDestroyView() {
        _vb = null
        super.onDestroyView()
    }
}

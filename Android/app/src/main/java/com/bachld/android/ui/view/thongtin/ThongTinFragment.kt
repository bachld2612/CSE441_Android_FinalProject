package com.bachld.android.ui.view.thongtin

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.load
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.response.auth.MyInfoResponse
import com.bachld.android.databinding.FragmentThongTinBinding
import com.bachld.android.ui.adapter.ProfileAdapter
import com.bachld.android.ui.adapter.ProfileRow
import com.bachld.android.ui.viewmodel.CvViewModel
import com.bachld.android.ui.viewmodel.ThongTinViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.bachld.android.databinding.DialogDoiMatKhauBinding
import androidx.appcompat.app.AlertDialog

class ThongTinFragment : Fragment(R.layout.fragment_thong_tin) {

    private var _binding: FragmentThongTinBinding? = null
    private val binding get() = _binding!!

    private val infoVm: ThongTinViewModel by viewModels()
    private val adapter = ProfileAdapter()

    private var changePwdDialog: AlertDialog? = null
    private val cvVm: CvViewModel by viewModels()

    // Picker CV (PDF)
    private val pickPdf = registerForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let { cvVm.uploadCv(requireContext(), it) }
    }

    // Picker ảnh
    private val pickImage = registerForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let { uploadAvatar(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentThongTinBinding.bind(view)


        val role = UserPrefs(requireContext()).getCached()?.role?.lowercase()
        val isGV = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"
        if (isGV) {
            // Giảng viên không cần upload CV
            binding.btnUploadCv.visibility = View.GONE
        } else {
            // Sinh viên mới có nút upload CV
            binding.btnUploadCv.visibility = View.VISIBLE
        }
        // Recycler
        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfile.adapter = adapter

        // Nút sửa -> mở picker
        binding.btnEdit.setOnClickListener { pickImage.launch("image/*") }

        binding.btnUploadCv.setOnClickListener {
            pickPdf.launch("application/pdf")
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // Load avatar từ cache nếu có
        UserPrefs(requireContext()).getCached()?.anhDaiDienUrl?.let { url ->
            if (url.isNotBlank()) {
                binding.imgAvatar.load(url) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_panda)
                    error(R.mipmap.ic_panda)
                    transformations(CircleCropTransformation())

                    listener(
                        onError = { _, result ->
                            // Xem log để biết lỗi nếu còn xảy ra
                            android.util.Log.e("ThongTin", "Coil load error", result.throwable)
                            Toast.makeText(requireContext(), "Không tải được ảnh: ${result.throwable.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Quan sát my-info
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    infoVm.changePasswordState.collect { st ->
                        when (st) {
                            is UiState.Loading -> Unit
                            is UiState.Success -> {
                                val res = st.data
                                when (res.code) {
                                    1000 -> {
                                        toast("Đổi mật khẩu thành công")
                                        changePwdDialog?.dismiss()
                                    }
                                    1007 -> toast("Mật khẩu cũ không chính xác")
                                    1046 -> toast("Mật khẩu mới không được trùng với mật khẩu cũ")
                                    else -> toast(res.message ?: "Đổi mật khẩu thất bại")
                                }
                                changePwdDialog?.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                                infoVm.clearChangePasswordState()
                            }
                            is UiState.Error -> {
                                toast(st.message ?: "Lỗi đổi mật khẩu")
                                changePwdDialog?.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
                                infoVm.clearChangePasswordState()
                            }
                            else -> Unit
                        }
                    }
                }

                // Quan sát các state từ ViewModel upload cv
                launch {
                    cvVm.cvState.collect { st ->
                        when (st) {
                            is UiState.Loading -> {
                                binding.btnUploadCv.isEnabled = false
                                Toast.makeText(requireContext(), "Đang upload CV...", Toast.LENGTH_SHORT).show()
                            }
                            is UiState.Success -> {
                                binding.btnUploadCv.isEnabled = true
                                val res = st.data
                                if (res.code == 1000) {
                                    Toast.makeText(requireContext(), "Upload CV thành công", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), res.message ?: "Upload CV thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                            is UiState.Error -> {
                                binding.btnUploadCv.isEnabled = true
                                Toast.makeText(requireContext(), st.message ?: "Lỗi upload CV", Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    infoVm.myInfoState.collect { st ->
                        when (st) {
                            is UiState.Success -> {
                                val res = st.data
                                val info = res.result
                                if (res.code == 1000 && info != null) {
                                    submitRows(info)
                                    info.anhDaiDienUrl?.let { url ->
                                        if (url.isNotBlank()) {
                                            binding.imgAvatar.load(url) {
                                                crossfade(true)
                                                placeholder(R.mipmap.ic_panda)
                                                error(R.mipmap.ic_panda)
                                                transformations(CircleCropTransformation())
                                                // lần hiển thị thông thường: dùng cache bình thường
                                            }
                                        }
                                    }
                                } else toast(res.message ?: "Không lấy được thông tin")

                            }
                            is UiState.Error -> toast(st.message ?: "Lỗi khi lấy thông tin")
                            else -> Unit
                        }
                    }
                }

                // Quan sát upload-state -> reload ảnh + reload my-info
                // Sau khi upload thành công:
                launch {
                    infoVm.uploadState.collect { st ->
                        when (st) {
                            is UiState.Success -> {
                                val url = st.data.result?.anhDaiDienUrl
                                if (!url.isNullOrBlank()) {
                                    // 1) Cập nhật cache local ngay -> user_prefs.xml đổi tức thì
                                    UserPrefs(requireContext()).updateAvatarUrl(url)

                                    // 2) Hiển thị ngay ảnh mới (bẻ cache bằng query cb=timestamp)
                                    val freshUrl =
                                        if ('?' in url) "$url&cb=${System.currentTimeMillis()}"
                                        else "$url?cb=${System.currentTimeMillis()}"

                                    binding.imgAvatar.load(freshUrl) {
                                        crossfade(true)
                                        placeholder(R.mipmap.ic_panda)
                                        error(R.mipmap.ic_panda)
                                        transformations(CircleCropTransformation())

                                        // chỉ tắt cache ở LẦN đầu sau upload để chắc chắn
                                        memoryCachePolicy(CachePolicy.DISABLED)
                                        diskCachePolicy(CachePolicy.DISABLED)
                                        networkCachePolicy(CachePolicy.DISABLED)

                                        listener(
                                            onError = { _, r ->
                                                android.util.Log.e("ThongTin", "Coil error", r.throwable)
                                            }
                                        )
                                    }

                                    // 3) Ép gọi lại my-info để đồng bộ tất cả field (forceRefresh=true)
                                    infoVm.loadMyInfo(requireContext(), forceRefresh = true)
                                    toast("Cập nhật ảnh thành công")
                                } else {
                                    toast(st.data.message ?: "Upload thất bại")
                                }
                                binding.btnEdit.isEnabled = true
                            }
                            is UiState.Error -> {
                                binding.btnEdit.isEnabled = true
                                toast(st.message ?: "Lỗi upload")
                            }
                            is UiState.Loading -> binding.btnEdit.isEnabled = false
                            else -> Unit
                        }
                    }
                }
            }
        }
        // Trigger load
        infoVm.loadMyInfo(requireContext())

        binding.btnLogout.setOnClickListener {
            binding.btnLogout.isEnabled = false
            infoVm.doLogout(requireContext())

            runCatching {
                findNavController().navigate(R.id.action_global_nav_auth)
            }.onFailure {
                requireActivity().finish()
            }
        }
    }

    private fun submitRows(info: MyInfoResponse) {
        val rows = mutableListOf<ProfileRow>()
        rows += ProfileRow("Họ và tên", info.hoTen.orEmpty())
        rows += ProfileRow("Email", info.email.orEmpty())
        rows += ProfileRow("Số điện thoại", info.soDienThoai.orEmpty())

        when (info.role?.lowercase()) {
            "sinh_vien" -> {
                rows += ProfileRow("Mã sinh viên", info.maSV.orEmpty())
                rows += ProfileRow("Lớp", info.lop.orEmpty())
                rows += ProfileRow("Ngành", info.nganh.orEmpty())
                rows += ProfileRow("Khoa", info.khoa.orEmpty())
            }
            "giang_vien", "truong_bo_mon", "tro_ly_khoa" -> {
                rows += ProfileRow("Mã giảng viên", info.maGV.orEmpty())
                rows += ProfileRow("Học vị", info.hocVi.orEmpty())
                rows += ProfileRow("Học hàm", info.hocHam.orEmpty())
                rows += ProfileRow("Bộ môn", info.boMon.orEmpty())
                rows += ProfileRow("Khoa", info.khoa.orEmpty())
            }
            else -> {
                if (!info.khoa.isNullOrBlank()) rows += ProfileRow("Đơn vị", info.khoa!!)
            }
        }
        adapter.submit(rows)
    }

    private fun uploadAvatar(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.btnEdit.isEnabled = false
                val bytes = compressJpegFromUri(requireContext(), uri)
                val part: MultipartBody.Part = buildCompressedPart(bytes)
                infoVm.uploadAnhDaiDien(part)
            } finally {
                // trạng thái sẽ bật lại khi nhận state Success/Error
            }
        }
    }

    private fun showChangePasswordDialog() {
        val db = DialogDoiMatKhauBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Đổi mật khẩu")
            .setView(db.root)
            .setNegativeButton("Trở về", null)
            .setPositiveButton("Đổi mật khẩu", null)
            .create()
        dialog.setOnShowListener {
            val positive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            positive.setOnClickListener {
                val current = db.etCurrent.text?.toString().orEmpty()
                val newPw = db.etNew.text?.toString().orEmpty()
                val confirm = db.etConfirm.text?.toString().orEmpty()

                if (current.length < 6 || newPw.length < 6 || confirm.length < 6) {
                    toast("Mật khẩu phải chứa ít nhất 6 kí tự")
                    return@setOnClickListener
                }
                if (newPw != confirm) {
                    toast("Xác nhận mật khẩu không khớp")
                    return@setOnClickListener
                }
                infoVm.changePassword(current, newPw)
                positive.isEnabled = false
            }
        }
        dialog.show()
        changePwdDialog = dialog
    }

    private fun toast(s: String) =
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        changePwdDialog?.dismiss()
        changePwdDialog = null
        _binding = null
        super.onDestroyView()
    }
}

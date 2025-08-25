package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.giangvien.GiangVienResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.GiangVienRepository
import com.bachld.android.data.repository.impl.GiangVienRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class DangKyDoAnViewModel(
    private val gvRepo: GiangVienRepository = GiangVienRepositoryImpl()
) : ViewModel() {

    private val _gvList = MutableStateFlow<List<GiangVienResponse>>(emptyList())
    val gvList: StateFlow<List<GiangVienResponse>> = _gvList

    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> = _registerState

    fun loadGVHDOnce() {
        if (_gvList.value.isNotEmpty()) return
        viewModelScope.launch {
            runCatching { gvRepo.getAllForDropdown() }
                .onSuccess { _gvList.value = it }
        }
    }

    fun submitRegistration(
        gvhdId: Long,
        tenDeTai: String,
        filePart: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            try {
                _registerState.value = UiState.Loading
                ApiClient.deTaiApi.registerDeTai(
                    gvhdId = gvhdId.toString().toRequestBody("text/plain".toMediaType()),
                    tenDeTai = tenDeTai.toRequestBody("text/plain".toMediaType()),
                    fileTongQuan = filePart
                ).result
                _registerState.value = UiState.Success(Unit)
            } catch (t: Throwable) {
                _registerState.value = UiState.Error(t.toDangKyMessage())
            }
        }
    }

    private fun Throwable.toDangKyMessage(): String {
        val byCode = mapOf(
            1043 to "Chưa đến hoặc đã qua thời gian đăng ký.",
            1215 to "Ngoài thời gian nộp/đăng ký.",
            1216 to "Chưa có đợt mở đăng ký.",
            1103 to "File tổng quan không hợp lệ.",
            1040 to "Định dạng file không được phép.",
            1041 to "File quá lớn.",
            1044 to "Đề tài đã được duyệt, không thể đăng ký lại.",
            1107 to "Tải tệp lên thất bại. Hãy thử lại."
        )
        return when (this) {
            is HttpException -> {
                val raw = response()?.errorBody()?.string()
                var code: Int? = null
                var msg: String? = null
                try {
                    raw?.let {
                        val j = JSONObject(it)
                        if (j.has("code")) code = j.optInt("code")
                        if (j.has("message")) msg = j.optString("message")
                    }
                } catch (_: Exception) {}

                val friendly = code?.let { byCode[it] } ?: msg
                val base = when (code()) {
                    400 -> friendly ?: "Yêu cầu không hợp lệ."
                    401 -> "Bạn chưa đăng nhập. Vui lòng đăng nhập lại."
                    403 -> friendly ?: "Bạn không có quyền thực hiện thao tác này."
                    404 -> friendly ?: "Không tìm thấy dữ liệu."
                    in 500..599 -> friendly ?: "Máy chủ gặp sự cố. Hãy thử lại sau."
                    else -> friendly ?: "Đã có lỗi xảy ra. Hãy thử lại."
                }
                if (code != null) "$base (#$code)" else base
            }
            is SocketTimeoutException -> "Kết nối hết thời gian chờ. Hãy thử lại."
            is IOException -> "Không thể kết nối máy chủ. Kiểm tra mạng rồi thử lại."
            else -> this.message ?: "Đã có lỗi xảy ra. Hãy thử lại."
        }
    }
}

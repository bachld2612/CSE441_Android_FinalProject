package com.bachld.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.hoidong.HoiDongDetailResponse
import com.bachld.android.data.repository.HoiDongRepository
import com.bachld.android.data.repository.impl.HoiDongRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class HoiDongDetailViewModel(
    private val repo: HoiDongRepository = HoiDongRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<HoiDongDetailResponse>>(UiState.Idle)
    val state: StateFlow<UiState<HoiDongDetailResponse>> = _state

    fun load(id: Long) {
        viewModelScope.launch {
            try {
                _state.value = UiState.Loading
                _state.value = UiState.Success(repo.getHoiDongDetail(id))
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.toHoiDongUserMessage())
            }
        }
    }

    private fun Throwable.toHoiDongUserMessage(): String {
        val friendlyByCode = mapOf(
            1118 to "Không tìm thấy hội đồng",
            1033 to "Đợt bảo vệ không tồn tại",
            1123 to "File Excel không hợp lệ",
            1124 to "Đề tài đã thuộc hội đồng khác trong đợt này",
            1125 to "Đề tài không thuộc đợt của hội đồng",
            1126 to "Đề tài chưa được duyệt",
            1024 to "Cấu hình hội đồng không hợp lệ"
        )

        return when (this) {
            is HttpException -> {
                val raw = response()?.errorBody()?.string()
                var code: Int? = null
                var backendMsg: String? = null
                try {
                    raw?.let {
                        val j = JSONObject(it)
                        if (j.has("code")) code = j.optInt("code")
                        if (j.has("message")) backendMsg = j.optString("message")
                    }
                } catch (_: Exception) { }

                val friendly = code?.let { friendlyByCode[it] } ?: backendMsg

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

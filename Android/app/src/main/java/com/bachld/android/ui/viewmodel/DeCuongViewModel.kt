// app/src/main/java/com/bachld/android/ui/viewmodel/DeCuongViewModel.kt
package com.bachld.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.dto.response.decuong.DeCuongState
import com.bachld.android.data.repository.DeCuongRepository
import com.bachld.android.data.repository.impl.DeCuongRepositoryImpl
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DeCuongViewModel(
    private val repository: DeCuongRepository = DeCuongRepositoryImpl()
) : ViewModel() {

    private val _logState = MutableLiveData<UiState<DeCuongLogResponse?>>(UiState.Idle)
    val logState: LiveData<UiState<DeCuongLogResponse?>> = _logState

    private val _submitState = MutableLiveData<UiState<DeCuongResponse?>>(UiState.Idle)
    val submitState: LiveData<UiState<DeCuongResponse?>> = _submitState

    // Trạng thái hiện tại (suy ra từ log)
    private val _currentState = MutableLiveData<DeCuongState?>(null)
    val currentState: LiveData<DeCuongState?> = _currentState

    fun loadLog() {
        _logState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = repository.viewLog()
                val log = res.result
                _logState.value = UiState.Success(log)

                // 👉 Điền mapping đúng theo DeCuongLogResponse của bạn:
                _currentState.value = log?.let { extractStateFrom(it) }
            } catch (e: Exception) {
                _logState.value = UiState.Error(e.message)
            }
        }
    }

    /** SỬA Ở ĐÂY cho đúng cấu trúc log của bạn */
    private fun extractStateFrom(log: DeCuongLogResponse): DeCuongState? {
        // Ví dụ các khả năng — chọn 1 cái khớp thực tế:
        // return log.trangThaiHienTai
        // return log.trangThai
        // return log.items?.maxByOrNull { it.ngayNop }?.trangThai
        // return if (log.daDuocDuyet == true) DeCuongState.ACCEPTED else DeCuongState.PENDING
        return null // nếu chưa biết map thế nào
    }

    fun submit(fileUrl: String) {
        // ✅ Chặn ở ViewModel: khi đã duyệt, báo lỗi ngay
        if (_currentState.value == DeCuongState.ACCEPTED) {
            _submitState.value = UiState.Error("Đề cương đã được duyệt. Không thể nộp thêm.")
            return
        }

        _submitState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val req = DeCuongUploadRequest(fileUrl.trim())
                val res = repository.submit(req)
                _submitState.value = UiState.Success(res.result)

                // Nếu response có trạng thái thì cập nhật
                _currentState.value = res.result?.trangThai

                // Làm tươi log (và lại suy ra state)
                loadLog()
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val apiErr = raw?.let { com.google.gson.Gson().fromJson(it, ApiResponse::class.java) }
                val code = apiErr?.code ?: 0
                _submitState.value = UiState.Error(code.toString())
                Log.d("DeCuongViewModel", "submit: ${_submitState.value}")
            }
        }
    }

    fun resetSubmitState() { _submitState.value = UiState.Idle }
}

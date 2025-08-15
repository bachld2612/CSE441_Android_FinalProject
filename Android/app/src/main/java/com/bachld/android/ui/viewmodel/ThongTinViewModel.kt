package com.bachld.android.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.auth.MyInfoResponse
import com.bachld.android.data.dto.response.taikhoan.AnhDaiDienUploadResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.AuthRepository
import com.bachld.android.data.repository.TaiKhoanRepository
import com.bachld.android.data.repository.impl.AuthRepositoryImpl
import com.bachld.android.data.repository.impl.TaiKhoanRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ThongTinViewModel(
    private val authRepo: AuthRepository = AuthRepositoryImpl(ApiClient.app),
    private val tkRepo: TaiKhoanRepository = TaiKhoanRepositoryImpl()
) : ViewModel() {

    private val _myInfoState =
        MutableStateFlow<UiState<ApiResponse<MyInfoResponse>>>(UiState.Idle)
    val myInfoState: StateFlow<UiState<ApiResponse<MyInfoResponse>>> = _myInfoState

    fun loadMyInfo(context: Context, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _myInfoState.value = UiState.Loading
                val prefs = UserPrefs(context)

                // Nếu không force, và cache còn mới -> dùng cache
                if (!forceRefresh && !prefs.isStale(UserPrefs.TTL_3H)) {
                    prefs.getCached()?.let {
                        _myInfoState.value =
                            UiState.Success(ApiResponse(code = 1000, message = "OK", result = it))
                        return@launch
                    }
                }

                // Gọi API
                val res = ApiClient.authApi.myInfo()
                if (res.code == 1000 && res.result != null) prefs.save(res.result)
                _myInfoState.value = UiState.Success(res)

            } catch (e: Exception) {
                _myInfoState.value = UiState.Error(e.message)
            }
        }
    }

    // Sau upload thành công, chỉ emit lên state cho Fragment xử lý cập nhật
    private val _uploadState =
        MutableStateFlow<UiState<ApiResponse<AnhDaiDienUploadResponse>>>(UiState.Idle)
    val uploadState: StateFlow<UiState<ApiResponse<AnhDaiDienUploadResponse>>> = _uploadState

    fun uploadAnhDaiDien(part: MultipartBody.Part) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            try {
                val res = ApiClient.taiKhoanApi.uploadAnhDaiDien(part)
                _uploadState.value = UiState.Success(res)
            } catch (e: Exception) {
                _uploadState.value = UiState.Error(e.message)
            }
        }
    }
}

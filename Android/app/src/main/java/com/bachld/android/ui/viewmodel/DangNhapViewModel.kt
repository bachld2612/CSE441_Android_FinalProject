package com.bachld.android.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.Session
import com.bachld.android.core.UiState
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.auth.LoginResponse
import com.bachld.android.data.dto.response.auth.MyInfoResponse
import com.bachld.android.data.repository.impl.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class DangNhapViewModel(app: Application): AndroidViewModel(app) {

    private val appContext = app.applicationContext
    private val prefs = UserPrefs(appContext)
    private val repo = AuthRepositoryImpl(appContext)
    private val _loginState = MutableStateFlow<UiState<ApiResponse<LoginResponse>>>(UiState.Idle)
    val loginState = _loginState
    private val _myInfoState = MutableStateFlow<UiState<ApiResponse<MyInfoResponse>>>(UiState.Idle)
    val myInfoState = _myInfoState

    fun login(loginRequest: LoginRequest) {
        Log.d("DangNhapVM", "Bắt đầu login với email=${loginRequest.email}")
        loginRequest.email = loginRequest.email?.trim()
        loginRequest.password = loginRequest.password?.trim()
        if (loginRequest.email.isNullOrEmpty() || loginRequest.password.isNullOrEmpty()) {
            _loginState.value = UiState.Error("Email or password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val res = repo.login(loginRequest)
                Log.d("DangNhapVM", "API trả code=${res.code}, message=${res.message}")
                if (res.code == 1000) {
                    Session.saveToken(res.result?.token ?: "")
                    _loginState.value = UiState.Success(res)

                    fetchMyInfo(forceRefresh = true)
                } else {
                    _loginState.value = UiState.Error(res.message)
                }
            } catch (t: Throwable) {
                _loginState.value = UiState.Error(t.message)
            }
        }
    }

    /** Đọc cache trước; nếu stale hoặc forceRefresh → gọi network rồi ghi cache. */
    fun fetchMyInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _myInfoState.value = UiState.Loading
            try {
                val cached = prefs.getCached()
                val stale = prefs.isStale(UserPrefs.TTL_3H)

                if (!forceRefresh && cached != null && !stale) {
                    _myInfoState.value = UiState.Success(
                        ApiResponse(code = 1000, message = "CACHE", result = cached)
                    )
                    return@launch
                }

                val res = repo.getMyInfo()
                if (res.code == 1000 && res.result != null) {
                    prefs.save(res.result)
                    _myInfoState.value = UiState.Success(res.copy(message = "NETWORK"))
                } else {
                    if (cached != null) {
                        _myInfoState.value = UiState.Success(
                            ApiResponse(code = 1000, message = "STALE_CACHE", result = cached)
                        )
                    } else {
                        _myInfoState.value = UiState.Error(res.message ?: "Lấy my-info thất bại")
                    }
                }
            } catch (t: Throwable) {
                val cached = prefs.getCached()
                if (cached != null) {
                    _myInfoState.value = UiState.Success(
                        ApiResponse(code = 1000, message = "STALE_CACHE", result = cached)
                    )
                } else {
                    _myInfoState.value = UiState.Error(t.message)
                }
            }
        }
    }

}
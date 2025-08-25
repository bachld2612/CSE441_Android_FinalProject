package com.bachld.android.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.repository.SinhVienRepository
import com.bachld.android.data.repository.impl.SinhVienRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CvViewModel(
    private val repo: SinhVienRepository = SinhVienRepositoryImpl()
) : ViewModel() {

    private val _cvState = MutableStateFlow<UiState<ApiResponse<String>>>(UiState.Idle)
    val cvState: StateFlow<UiState<ApiResponse<String>>> = _cvState

    fun uploadCv(context: Context, uri: Uri) {
        _cvState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = repo.uploadCv(context, uri)
                _cvState.value = UiState.Success(res)
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string() // chỉ đọc 1 lần!
                val apiErr = raw?.let { Gson().fromJson(it, ApiResponse::class.java) }
                val code = apiErr?.code ?: e.code()
                _cvState.value = UiState.Error(code.toString())
            }
        }
    }
}
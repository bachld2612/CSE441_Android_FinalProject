package com.bachld.android.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.repository.SinhVienRepository
import com.bachld.android.data.repository.impl.SinhVienRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
            } catch (e: Exception) {
                _cvState.value = UiState.Error(e.message)
            }
        }
    }
}
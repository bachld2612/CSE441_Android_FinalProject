package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.*
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.repository.DeCuongRepository
import com.bachld.android.data.repository.impl.DeCuongRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

typealias DeCuongListRes = ApiResponse<PageData<DeCuongItem>>
typealias DeCuongActionRes = ApiResponse<DeCuongActionResponse>

class DeCuongGvViewModel(
    private val repo: DeCuongRepository = DeCuongRepositoryImpl()
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<DeCuongListRes>>(UiState.Idle)
    val listState: StateFlow<UiState<DeCuongListRes>> = _listState

    private val _actionState = MutableStateFlow<UiState<DeCuongActionRes>>(UiState.Idle)
    val actionState: StateFlow<UiState<DeCuongActionRes>> = _actionState

    fun load(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _listState.value = UiState.Loading
            try {
                val res = repo.fetch(page, size)
                _listState.value = UiState.Success(res)
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val apiErr = raw?.let { com.google.gson.Gson().fromJson(it, ApiResponse::class.java) }
                val code = apiErr?.message ?: e.code()
                _listState.value = UiState.Error(code.toString())
            }
        }
    }

    fun approve(id: Long) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = repo.approve(id)
                if (res.code == 1000) _actionState.value = UiState.Success(res)
                else _actionState.value = UiState.Error(res.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message)
            }
        }
    }

    fun reject(id: Long, reason: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = repo.reject(id, reason)
                if (res.code == 1000) _actionState.value = UiState.Success(res)
                else _actionState.value = UiState.Error(res.message)
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.message)
            }
        }
    }

    fun clearAction() { _actionState.value = UiState.Idle }
}

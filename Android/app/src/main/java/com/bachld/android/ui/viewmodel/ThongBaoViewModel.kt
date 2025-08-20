package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.thongbao.ThongBaoResponse
import com.bachld.android.data.repository.impl.ThongBaoRepositoryImpl
import kotlinx.coroutines.launch

class ThongBaoViewModel : ViewModel() {
    private val repo = ThongBaoRepositoryImpl()

    private val _uiState = MutableLiveData<UiState<List<ThongBaoResponse>>>(UiState.Idle)
    val uiState: LiveData<UiState<List<ThongBaoResponse>>> get() = _uiState

    // Khởi tạo là Idle để observer không bắn nhầm
    private val _detailState = MutableLiveData<UiState<ThongBaoResponse>>(UiState.Idle)
    val detailState: LiveData<UiState<ThongBaoResponse>> get() = _detailState

    fun fetchThongBao() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = repo.getThongBao()
                if (response.code == 1000) {
                    val list = response.result?.content ?: emptyList()
                    _uiState.value = UiState.Success(list)
                } else {
                    _uiState.value = UiState.Error("Lỗi code: ${response.code}")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }

    fun fetchThongBaoDetail(id: Long) {
        _detailState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = repo.getThongBaoDetail(id)
                if (response.code == 1000 && response.result != null) {
                    _detailState.value = UiState.Success(response.result)
                } else {
                    _detailState.value = UiState.Error("Không tìm thấy thông báo $id")
                }
            } catch (e: Exception) {
                _detailState.value = UiState.Error(e.message)
            }
        }
    }

    // GỌN: gọi sau khi đã navigate xong để tránh re-navigate khi quay lại
    fun clearDetailState() {
        _detailState.value = UiState.Idle
    }
}

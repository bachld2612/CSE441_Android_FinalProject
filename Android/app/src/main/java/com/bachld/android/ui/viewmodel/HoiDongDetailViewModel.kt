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
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message)
            }
        }
    }
}

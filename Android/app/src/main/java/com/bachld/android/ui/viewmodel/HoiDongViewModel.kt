package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.hoidong.HoiDongListItemResponse
import com.bachld.android.data.repository.HoiDongRepository
import com.bachld.android.data.repository.impl.HoiDongRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HoiDongViewModel(
    private val repo: HoiDongRepository = HoiDongRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<HoiDongListItemResponse>>>(UiState.Idle)
    val state: StateFlow<UiState<List<HoiDongListItemResponse>>> = _state

    fun load(keyword: String? = null) {
        viewModelScope.launch {
            try {
                _state.value = UiState.Loading
                val list = repo.getHoiDongs(keyword)
                _state.value = UiState.Success(list)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message)
            }
        }
    }
}
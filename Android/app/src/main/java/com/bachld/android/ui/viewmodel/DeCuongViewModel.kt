// app/src/main/java/com/bachld/android/ui/viewmodel/DeCuongViewModel.kt
package com.bachld.android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.repository.DeCuongRepository
import com.bachld.android.data.repository.impl.DeCuongRepositoryImpl
import kotlinx.coroutines.launch

class DeCuongViewModel(
    private val repository: DeCuongRepository = DeCuongRepositoryImpl()
) : ViewModel() {

    private val _logState = MutableLiveData<UiState<DeCuongLogResponse?>>(UiState.Idle)
    val logState: LiveData<UiState<DeCuongLogResponse?>> = _logState

    private val _submitState = MutableLiveData<UiState<DeCuongResponse?>>(UiState.Idle)
    val submitState: LiveData<UiState<DeCuongResponse?>> = _submitState

    fun loadLog() {
        _logState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = repository.viewLog()
                _logState.value = UiState.Success(res.result)
            } catch (e: Exception) {
                _logState.value = UiState.Error(e.message)
            }
        }
    }

    fun submit(fileUrl: String) {
        _submitState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val request = DeCuongUploadRequest(fileUrl.trim())
                val res = repository.submit(request)
                _submitState.value = UiState.Success(res.result)
                // Sau khi nộp -> làm tươi log
                loadLog()
            } catch (e: Exception) {
                _submitState.value = UiState.Error(e.message)
            }
        }
    }

    fun resetSubmitState() { _submitState.value = UiState.Idle }
}

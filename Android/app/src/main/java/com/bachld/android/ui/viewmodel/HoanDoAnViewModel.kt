package com.bachld.android.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.request.donhoandoan.DonHoanDoAnRequest
import com.bachld.android.data.dto.response.donhoandoan.DonHoanDoAnResponse
import com.bachld.android.data.repository.DonHoanDoAnRepository
import com.bachld.android.data.repository.impl.DonHoanDoAnRepositoryImpl
import kotlinx.coroutines.launch

class HoanDoAnViewModel(
    private val repo: DonHoanDoAnRepository = DonHoanDoAnRepositoryImpl()
) : ViewModel() {

    val submitState = MutableLiveData<UiState<DonHoanDoAnResponse?>>(UiState.Idle)

    fun submit(lyDo: String, minhChungUri: Uri?) {
        submitState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = repo.create(DonHoanDoAnRequest(lyDo, minhChungUri))
                submitState.value = UiState.Success(res.result)
            } catch (t: Throwable) {
                submitState.value = UiState.Error(t.message ?: "Gửi thất bại")
            }
        }
    }

    fun reset() { submitState.value = UiState.Idle }
}

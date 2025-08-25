package com.bachld.android.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.detai.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.dto.response.giangvien.*
import com.bachld.android.data.repository.GiangVienRepository
import com.bachld.android.data.repository.impl.GiangVienRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

typealias DeTaiListPage = ApiResponse<PageData<DeTaiXetDuyetResponse>>
typealias DeTaiActionRes = ApiResponse<DeTaiResponse>

class GVXetDuyetViewModel(
    private val repo: GiangVienRepository = GiangVienRepositoryImpl()
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<DeTaiListPage>>(UiState.Idle)
    val listState: StateFlow<UiState<DeTaiListPage>> = _listState

    private val _actionState = MutableStateFlow<UiState<DeTaiActionRes>>(UiState.Idle)
    val actionState: StateFlow<UiState<DeTaiActionRes>> = _actionState

    fun load(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _listState.value = UiState.Loading
            try {
                val res = repo.fetchDeTai(page, size)
                _listState.value = UiState.Success(res)
            } catch (e: Exception) {
                _listState.value = UiState.Error(e.message)
            }
        }
    }

    fun approve(idDeTai: Long) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = repo.approveDeTai(idDeTai) // ApiResponse<DeTaiResponse>
                if (res.code == 1000) _actionState.value = UiState.Success(res)
                else _actionState.value = UiState.Error(res.message)
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val apiErr = raw?.let { com.google.gson.Gson().fromJson(it, ApiResponse::class.java) }
                _actionState.value = UiState.Success(
                    ApiResponse(
                        code = apiErr?.code ?: e.code(),
                        message = apiErr?.message ?: e.message(),
                        result = null
                    )
                )
            }
        }
    }

    fun reject(idDeTai: Long, lyDo: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = repo.rejectDeTai(idDeTai, lyDo)
                if (res.code == 1000) _actionState.value = UiState.Success(res)
                else _actionState.value = UiState.Error(res.message)
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val apiErr = raw?.let { com.google.gson.Gson().fromJson(it, ApiResponse::class.java) }
                _actionState.value = UiState.Success(
                    ApiResponse(
                        code = apiErr?.code ?: e.code(),
                        message = apiErr?.message ?: e.message(),
                        result = null
                    )
                )
            }
        }
    }

    fun clearAction() { _actionState.value = UiState.Idle }
}
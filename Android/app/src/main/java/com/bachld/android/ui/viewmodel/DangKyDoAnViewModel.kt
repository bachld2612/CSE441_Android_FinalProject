package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.data.dto.response.giangvien.GiangVienResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.GiangVienRepository
import com.bachld.android.data.repository.impl.GiangVienRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class DangKyDoAnViewModel(
    private val gvRepo: GiangVienRepository = GiangVienRepositoryImpl()
) : ViewModel() {

    private val _gvList = MutableStateFlow<List<GiangVienResponse>>(emptyList())
    val gvList: StateFlow<List<GiangVienResponse>> = _gvList

    fun loadGVHDOnce() {
        if (_gvList.value.isNotEmpty()) return
        viewModelScope.launch {
            runCatching { gvRepo.getAllForDropdown() }
                .onSuccess { _gvList.value = it }
        }
    }

    suspend fun registerDeTai(
        gvhdId: Long,
        tenDeTai: String,
        filePart: MultipartBody.Part?
    ) = ApiClient.deTaiApi.registerDeTai(
        gvhdId = gvhdId.toString().toRequestBody("text/plain".toMediaType()),
        tenDeTai = tenDeTai.toRequestBody("text/plain".toMediaType()),
        fileTongQuan = filePart
    ).result
}

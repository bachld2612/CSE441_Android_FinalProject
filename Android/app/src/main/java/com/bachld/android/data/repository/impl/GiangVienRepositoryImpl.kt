package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.request.giangvien.RejectDeTaiRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.DeTaiXetDuyetResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.GiangVienRepository

class GiangVienRepositoryImpl: GiangVienRepository {
    private val api = ApiClient.giangVienApi

    override suspend fun fetchDeTai(page: Int, size: Int): ApiResponse<PageData<DeTaiXetDuyetResponse>> {
        return api.getXetDuyetDeTai(page, size)
    }

    override suspend fun approveDeTai(idDeTai: Long): ApiResponse<DeTaiResponse> =
        api.approveDeTai(idDeTai)

    override suspend fun rejectDeTai(idDeTai: Long, lyDo: String): ApiResponse<DeTaiResponse> =
        api.rejectDeTai(idDeTai, RejectDeTaiRequest(lyDo))
}
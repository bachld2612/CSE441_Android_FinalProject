package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.request.giangvien.RejectDeTaiRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.DeTaiXetDuyetResponse
import com.bachld.android.data.dto.response.PageData
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.GiangVienRepository
import com.bachld.android.data.dto.response.giangvien.GiangVienResponse

class GiangVienRepositoryImpl: GiangVienRepository {
    private val api = ApiClient.giangVienApi

    override suspend fun fetchDeTai(page: Int, size: Int): ApiResponse<PageData<DeTaiXetDuyetResponse>> {
        return api.getXetDuyetDeTai(page, size)
    }

    override suspend fun approveDeTai(idDeTai: Long): ApiResponse<DeTaiResponse> =
        api.approveDeTai(idDeTai)

    override suspend fun rejectDeTai(idDeTai: Long, lyDo: String): ApiResponse<DeTaiResponse> =
        api.rejectDeTai(idDeTai, RejectDeTaiRequest(lyDo))
    override suspend fun getAllForDropdown(): List<GiangVienResponse> {
        val page = api.listGiangVien(size = 200).result
        return page?.content ?: emptyList()
    }
}
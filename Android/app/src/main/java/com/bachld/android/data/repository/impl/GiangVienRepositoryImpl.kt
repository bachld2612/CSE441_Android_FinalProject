package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.request.giangvien.RejectDeTaiRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.DeTaiXetDuyetResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.dto.response.unwrapOrThrow
import com.bachld.android.data.model.SupervisedStudent
import com.bachld.android.data.model.mapper.to_model
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

    override suspend fun get_sinh_vien_huong_dan_all(q: String?)
            : List<SupervisedStudent> {
        val res = api.get_sinh_vien_huong_dan_all(q)
        val dtos = res.unwrapOrThrow()
        return dtos.map { it.to_model() }
    }
}
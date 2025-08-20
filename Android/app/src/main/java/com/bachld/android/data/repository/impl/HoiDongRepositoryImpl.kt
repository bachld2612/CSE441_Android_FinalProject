package com.bachld.android.data.repository.impl

import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.HoiDongRepository
import com.bachld.android.data.dto.response.hoidong.*

class HoiDongRepositoryImpl : HoiDongRepository {

    private val api = ApiClient.hoiDongApi

    override suspend fun getHoiDongs(keyword: String?): List<HoiDongListItemResponse> {
        val res = api.getHoiDongs(keyword = keyword)
        return res.result?.content.orEmpty()
    }

    override suspend fun getHoiDongDetail(id: Long): HoiDongDetailResponse {
        val res = api.getHoiDongDetail(id)
        return res.result ?: error("Chi tiết hội đồng rỗng từ server")
    }
}

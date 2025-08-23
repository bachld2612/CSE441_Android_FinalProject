package com.bachld.android.data.repository

import com.bachld.android.data.dto.response.hoidong.*

interface HoiDongRepository {
    suspend fun getHoiDongs(keyword: String?): List<HoiDongListItemResponse>
    suspend fun getHoiDongDetail(id: Long): HoiDongDetailResponse
}

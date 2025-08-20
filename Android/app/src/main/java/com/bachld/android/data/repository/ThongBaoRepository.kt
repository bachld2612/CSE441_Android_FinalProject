package com.bachld.android.data.repository

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.thongbao.PageResponse
import com.bachld.android.data.dto.response.thongbao.ThongBaoResponse
import com.bachld.android.data.remote.service.ThongBaoApi

interface ThongBaoRepository {


    suspend fun getThongBao(): ApiResponse<PageResponse<ThongBaoResponse>>
    suspend fun getThongBaoDetail(id: Long): ApiResponse<ThongBaoResponse>

}
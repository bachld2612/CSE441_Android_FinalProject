package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.thongbao.PageResponse
import com.bachld.android.data.dto.response.thongbao.ThongBaoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ThongBaoApi {

    @GET("/api/v1/thong-bao")
    suspend fun getThongBao(): ApiResponse<PageResponse<ThongBaoResponse>>

    @GET("/api/v1/thong-bao/{id}")
    suspend fun getThongBaoDetail(@Path("id") id: Long): ApiResponse<ThongBaoResponse>

}
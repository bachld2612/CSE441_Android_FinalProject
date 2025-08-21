package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.hoidong.PageResponse
import com.bachld.android.data.dto.response.hoidong.HoiDongDetailResponse
import com.bachld.android.data.dto.response.hoidong.HoiDongListItemResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HoiDongApi {

    @GET("api/v1/hoi-dong")
    suspend fun getHoiDongs(
        @Query("keyword") keyword: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<HoiDongListItemResponse>>

    @GET("api/v1/hoi-dong/{id}")
    suspend fun getHoiDongDetail(
        @Path("id") id: Long
    ): ApiResponse<HoiDongDetailResponse>
}

package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongActionResponse
import com.bachld.android.data.dto.response.decuong.DeCuongItem
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DeCuongApi {

    // Xem log cho SV
    @GET("api/v1/de-cuong/sinh-vien/log")
    suspend fun viewLog(): ApiResponse<DeCuongLogResponse>

    // Nộp đề cương: file đa phương tiện hoặc URL
    @Multipart
    @POST("api/v1/de-cuong/sinh-vien/nop-de-cuong")
    suspend fun submitDeCuong(
        @Part("fileUrl") fileUrl: RequestBody
    ): ApiResponse<DeCuongResponse>

    @GET("/api/v1/de-cuong")
    suspend fun getDeCuong(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        // ví dụ mặc định như swagger: updatedAt,DESC
        @Query("sort") sort: List<String> = listOf("updatedAt,DESC")
    ): ApiResponse<PageData<DeCuongItem>>

    @PUT("/api/v1/de-cuong/{id}/duyet")
    suspend fun approve(
        @Path("id") id: Long
    ): ApiResponse<DeCuongActionResponse>

    @PUT("/api/v1/de-cuong/{id}/tu-choi")
    suspend fun reject(
        @Path("id") id: Long,
        @Query("reason") reason: String
    ): ApiResponse<DeCuongActionResponse>
}

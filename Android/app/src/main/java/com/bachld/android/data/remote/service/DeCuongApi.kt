package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
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
}

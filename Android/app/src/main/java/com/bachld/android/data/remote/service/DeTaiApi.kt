package com.bachld.android.data.remote.service


import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.detai.DeTaiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DeTaiApi {
    @GET("api/v1/de-tai/chi-tiet")
    suspend fun getMyDeTai(): ApiResponse<DeTaiResponse>

    @Multipart
    @POST("api/v1/de-tai/dang-ky")
    suspend fun registerDeTai(
        @Part("gvhdId") gvhdId: RequestBody,
        @Part("tenDeTai") tenDeTai: RequestBody,
        @Part fileTongQuan: MultipartBody.Part? = null
    ): ApiResponse<DeTaiResponse>
}
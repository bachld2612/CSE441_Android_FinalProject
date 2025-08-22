package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.donhoandoan.DonHoanDoAnResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DonHoanDoAnApi {

    @Multipart
    @POST("api/v1/don-hoan/sinh-vien/hoan-do-an")
    suspend fun createPostponeRequest(
        @Part("lyDo") lyDo: RequestBody,
        @Part minhChungFile: MultipartBody.Part? = null
    ): ApiResponse<DonHoanDoAnResponse>
}

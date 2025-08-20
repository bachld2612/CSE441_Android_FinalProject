package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SinhVienApi {

    @Multipart
    @POST("api/v1/sinh-vien/upload-cv")
    suspend fun uploadCV(@Part file: MultipartBody.Part): ApiResponse<String>

}
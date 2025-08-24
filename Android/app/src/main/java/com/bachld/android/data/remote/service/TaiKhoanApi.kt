package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.taikhoan.AnhDaiDienUploadResponse
import com.bachld.android.data.dto.response.taikhoan.DoiMatKhauRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TaiKhoanApi {

    @Multipart
    @POST("api/v1/tai-khoan/anh-dai-dien")
    suspend fun uploadAnhDaiDien(
        @Part file: MultipartBody.Part
    ): ApiResponse<AnhDaiDienUploadResponse>

    @POST("api/v1/tai-khoan/doi-mat-khau")
    suspend fun doiMatKhau(@Body req: DoiMatKhauRequest): ApiResponse<String>
}

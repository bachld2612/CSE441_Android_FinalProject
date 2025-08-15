package com.bachld.android.data.repository

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.taikhoan.AnhDaiDienUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Part

interface TaiKhoanRepository {
    suspend fun uploadAnhDaiDien(@Part filePart: MultipartBody.Part): ApiResponse<AnhDaiDienUploadResponse>
}

package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.taikhoan.AnhDaiDienUploadResponse
import com.bachld.android.data.dto.response.taikhoan.DoiMatKhauRequest
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.TaiKhoanRepository
import okhttp3.MultipartBody

class TaiKhoanRepositoryImpl : TaiKhoanRepository {
    override suspend fun uploadAnhDaiDien(
        part: MultipartBody.Part
    ): ApiResponse<AnhDaiDienUploadResponse> {
        return ApiClient.taiKhoanApi.uploadAnhDaiDien(part)
    }

    override suspend fun doiMatKhau(req: DoiMatKhauRequest): ApiResponse<String> {
        return ApiClient.taiKhoanApi.doiMatKhau(req)
    }
}

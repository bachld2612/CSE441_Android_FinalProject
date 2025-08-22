package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.remote.service.DeCuongApi
import com.bachld.android.data.repository.DeCuongRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class DeCuongRepositoryImpl(
    private val api: DeCuongApi = ApiClient.deCuongApi
) : DeCuongRepository {

    override suspend fun viewLog(): ApiResponse<DeCuongLogResponse> {
        return api.viewLog()
    }

    override suspend fun submit(request: DeCuongUploadRequest): ApiResponse<DeCuongResponse> {
        val url = request.fileUrl.trim()
        require(url.isNotEmpty()) { "Bạn cần nhập URL đề cương." }

        // Không cần extension: tạo RequestBody text/plain trực tiếp
        val fileUrlBody = url.toRequestBody("text/plain".toMediaType())

        return api.submitDeCuong(
            fileUrl = fileUrlBody
        )
    }
}

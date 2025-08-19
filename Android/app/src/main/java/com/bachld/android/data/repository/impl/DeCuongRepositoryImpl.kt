package com.bachld.android.data.repository.impl

import android.app.Application
import android.net.Uri
import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.remote.service.DeCuongApi
import com.bachld.android.data.repository.DeCuongRepository
import com.bachld.android.util.toPlainRequestBody
import com.bachld.android.util.uriToMultipart
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DeCuongRepositoryImpl(
    private val api: DeCuongApi = ApiClient.deCuongApi,
    private val app: Application = ApiClient.app
) : DeCuongRepository {

    override suspend fun viewLog(): ApiResponse<DeCuongLogResponse> {
        return api.viewLog()
    }

    override suspend fun submit(request: DeCuongUploadRequest): ApiResponse<DeCuongResponse> {
        val deTaiIdBody: RequestBody = request.deTaiId.toString().toPlainRequestBody()

        val filePart: MultipartBody.Part? = request.fileUri?.let { uri: Uri ->
            uriToMultipart(app, uri, "file") // tên part phải là "file"
        }

        val fileUrlBody: RequestBody? = request.fileUrl
            ?.takeIf { it.isNotBlank() }
            ?.toPlainRequestBody() // tên part "fileUrl"

        if (filePart == null && fileUrlBody == null) {
            throw IllegalArgumentException("Bạn cần chọn tệp hoặc nhập URL đề cương.")
        }

        return api.submitDeCuong(
            deTaiId = deTaiIdBody,
            file = filePart,
            fileUrl = fileUrlBody
        )
    }
}

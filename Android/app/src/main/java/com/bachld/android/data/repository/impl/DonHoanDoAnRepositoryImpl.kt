package com.bachld.android.data.repository.impl

import android.app.Application
import com.bachld.android.data.dto.request.donhoandoan.DonHoanDoAnRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.donhoandoan.DonHoanDoAnResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.remote.service.DonHoanDoAnApi
import com.bachld.android.data.repository.DonHoanDoAnRepository
import com.bachld.android.util.toPlainRequestBody
import com.bachld.android.util.uriToMultipart
import okhttp3.MultipartBody

class DonHoanDoAnRepositoryImpl(
    private val api: DonHoanDoAnApi = ApiClient.donHoanDoAnApi,
    private val app: Application = ApiClient.app
) : DonHoanDoAnRepository {

    override suspend fun create(request: DonHoanDoAnRequest): ApiResponse<DonHoanDoAnResponse> {
        val lyDoBody = request.lyDo.trim().toPlainRequestBody()

        val filePart: MultipartBody.Part? = request.minhChungUri?.let { uri ->
            // tên part phải khớp backend: minhChungFile
            uriToMultipart(app, uri, "minhChungFile")
        }

        return api.createPostponeRequest(
            lyDo = lyDoBody,
            minhChungFile = filePart
        )
    }
}

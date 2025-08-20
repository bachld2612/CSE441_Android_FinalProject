package com.bachld.android.data.repository.impl

import android.content.Context
import android.net.Uri
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.SinhVienRepository
import com.bachld.android.utils.PdfPartUtil

class SinhVienRepositoryImpl: SinhVienRepository {

    private val sinhVienApi = ApiClient.sinhVienApi

    override suspend fun uploadCv(
        context: Context,
        uri: Uri
    ): ApiResponse<String> {
        val part = PdfPartUtil.createPdfPart(context, uri, fieldName = "file")
        return sinhVienApi.uploadCV(part)
    }
}
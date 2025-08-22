package com.bachld.android.data.repository

import android.content.Context
import android.net.Uri
import com.bachld.android.data.dto.response.ApiResponse

interface SinhVienRepository {

    suspend fun uploadCv(context: Context, uri: Uri): ApiResponse<String>

}
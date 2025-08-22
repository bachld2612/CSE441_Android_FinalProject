package com.bachld.android.data.repository

import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse

interface DeCuongRepository {
    suspend fun viewLog(): ApiResponse<DeCuongLogResponse>
    suspend fun submit(request: DeCuongUploadRequest): ApiResponse<DeCuongResponse>
}

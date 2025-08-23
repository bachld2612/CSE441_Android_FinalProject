package com.bachld.android.data.repository

import com.bachld.android.data.dto.request.decuong.DeCuongUploadRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.decuong.DeCuongActionResponse
import com.bachld.android.data.dto.response.decuong.DeCuongItem
import com.bachld.android.data.dto.response.decuong.DeCuongLogResponse
import com.bachld.android.data.dto.response.decuong.DeCuongResponse
import com.bachld.android.data.dto.response.giangvien.PageData

interface DeCuongRepository {
    suspend fun viewLog(): ApiResponse<DeCuongLogResponse>
    suspend fun submit(request: DeCuongUploadRequest): ApiResponse<DeCuongResponse>

    suspend fun fetch(page: Int, size: Int): ApiResponse<PageData<DeCuongItem>>
    suspend fun approve(id: Long): ApiResponse<DeCuongActionResponse>
    suspend fun reject(id: Long, reason: String): ApiResponse<DeCuongActionResponse>
}

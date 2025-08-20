package com.bachld.android.data.repository

import com.bachld.android.data.dto.request.donhoandoan.DonHoanDoAnRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.donhoandoan.DonHoanDoAnResponse

interface DonHoanDoAnRepository {
    suspend fun create(request: DonHoanDoAnRequest): ApiResponse<DonHoanDoAnResponse>
}

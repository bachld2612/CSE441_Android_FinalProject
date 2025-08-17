package com.bachld.android.data.remote.service


import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.DeTaiResponse
import retrofit2.http.GET

interface DeTaiApi {
    @GET("api/v1/de-tai/me")
    suspend fun getMyDeTai(): ApiResponse<DeTaiResponse>
}
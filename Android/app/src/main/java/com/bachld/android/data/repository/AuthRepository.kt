package com.bachld.android.data.repository

import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.auth.LoginResponse
import com.bachld.android.data.dto.response.auth.MyInfoResponse

interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): ApiResponse<LoginResponse>
    suspend fun getMyInfo(): ApiResponse<MyInfoResponse>

}
package com.bachld.android.data.repository.impl

import android.content.Context
import com.bachld.android.data.dto.request.auth.IntrospectRequest
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.auth.IntrospectResponse
import com.bachld.android.data.dto.response.auth.LoginResponse
import com.bachld.android.data.dto.response.auth.MyInfoResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.remote.service.AuthApi
import com.bachld.android.data.repository.AuthRepository

class AuthRepositoryImpl(
    private val context: Context,
    private val authApi: AuthApi = ApiClient.authApi
    ): AuthRepository {

        override suspend fun login(
            loginRequest: LoginRequest
        ): ApiResponse<LoginResponse> {
            return authApi.login(loginRequest)
        }

        override suspend fun getMyInfo(): ApiResponse<MyInfoResponse> {
            return authApi.myInfo()
        }

        override suspend fun introspect(loginRequest: IntrospectRequest): ApiResponse<IntrospectResponse> {
            return authApi.introspect(loginRequest)
        }
}
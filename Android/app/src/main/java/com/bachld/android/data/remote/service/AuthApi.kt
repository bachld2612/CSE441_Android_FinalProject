package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.request.auth.IntrospectRequest
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.data.dto.request.auth.LogoutRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.auth.IntrospectResponse
import com.bachld.android.data.dto.response.auth.LoginResponse
import com.bachld.android.data.dto.response.auth.MyInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("No-Auth: true")
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @Headers("No-Auth: true")
    @POST("api/v1/auth/introspect")
    suspend fun introspect(@Body request: IntrospectRequest): ApiResponse<IntrospectResponse>

    @GET("api/v1/auth/my-info")
    suspend fun myInfo(): ApiResponse<MyInfoResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body req: LogoutRequest): ApiResponse<String>
}
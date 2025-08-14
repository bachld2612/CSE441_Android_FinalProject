package com.bachld.android.data.dto.request.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") var email: String? = null,
    @Json(name = "matKhau") var password: String? = null
)
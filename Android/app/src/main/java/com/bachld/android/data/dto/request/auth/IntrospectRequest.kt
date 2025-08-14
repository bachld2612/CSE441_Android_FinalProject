package com.bachld.android.data.dto.request.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IntrospectRequest (@Json(name = "token") var token: String? = null)
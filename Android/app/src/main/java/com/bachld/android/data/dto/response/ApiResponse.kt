package com.bachld.android.data.dto.response

data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val result: T?
)
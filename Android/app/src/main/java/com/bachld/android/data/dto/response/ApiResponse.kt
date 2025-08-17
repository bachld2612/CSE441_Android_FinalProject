package com.bachld.android.data.dto.response

data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val result: T?
)

const val API_SUCCESS = 1000

val <T> ApiResponse<T>.isSuccess: Boolean
    get() = code == API_SUCCESS

fun <T> ApiResponse<T>.unwrapOrThrow(): T {
    if (!isSuccess) throw IllegalStateException(message ?: "Request failed (code=$code)")
    return result ?: throw IllegalStateException("Empty result (code=$code)")
}
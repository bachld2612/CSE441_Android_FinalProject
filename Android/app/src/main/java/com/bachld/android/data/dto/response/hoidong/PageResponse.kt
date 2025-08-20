package com.bachld.android.data.dto.response.hoidong

data class PageResponse<T>(
    val content: List<T> = emptyList()
)
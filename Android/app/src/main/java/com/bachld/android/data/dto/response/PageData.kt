package com.bachld.android.data.dto.response

data class PageData<T>(
    val content: List<T> = emptyList(),
    val last: Boolean = true,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val number: Int = 0,
    val size: Int = 0,
    val first: Boolean = true,
    val numberOfElements: Int = 0,
    val empty: Boolean = true
)
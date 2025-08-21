package com.bachld.android.data.dto.response.common
import com.squareup.moshi.Json

data class PageDto<T>(
    @Json(name = "content") val content: List<T>,
    @Json(name = "number") val number: Int,
    @Json(name = "size") val size: Int,
    @Json(name = "totalElements") val totalElements: Long,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "first") val first: Boolean,
    @Json(name = "last") val last: Boolean
)

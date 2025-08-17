package com.bachld.android.data.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeTaiResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "tenDeTai") val title: String,
    @Json(name = "trangThai") val status: String,
    @Json(name = "nhanXet") val review: String? = null,
    @Json(name = "gvhdId") val advisorId: Long,
    @Json(name = "gvhdTen") val advisorName: String?,
    @Json(name = "sinhVienId") val studentId: Long? = null,
    @Json(name = "tongQuanDeTaiUrl") val overviewUrl: String? = null
)
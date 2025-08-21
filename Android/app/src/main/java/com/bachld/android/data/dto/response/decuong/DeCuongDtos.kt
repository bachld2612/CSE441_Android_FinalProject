package com.bachld.android.data.dto.response.decuong

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Item dùng cho GET /api/v1/de-cuong
@JsonClass(generateAdapter = true)
data class DeCuongItem(
    @Json(name = "id") val id: Long,
    @Json(name = "deCuongUrl") val url: String?,
    @Json(name = "trangThai") val status: String,          // PENDING | ACCEPTED | CANCELLED
    @Json(name = "soLanNop") val turn: Int?,
    @Json(name = "tenDeTai") val topicTitle: String,
    @Json(name = "maSV") val studentCode: String,
    @Json(name = "hoTenSinhVien") val studentName: String,
    @Json(name = "hoTenGiangVien") val teacherName: String
)

// Dùng cho PUT duyệt / từ chối (có thêm nhanXet)
@JsonClass(generateAdapter = true)
data class DeCuongActionResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "deCuongUrl") val url: String?,
    @Json(name = "trangThai") val status: String,
    @Json(name = "soLanNop") val turn: Int?,
    @Json(name = "nhanXet") val review: String?,
    @Json(name = "tenDeTai") val topicTitle: String,
    @Json(name = "maSV") val studentCode: String,
    @Json(name = "hoTenSinhVien") val studentName: String,
    @Json(name = "hoTenGiangVien") val teacherName: String
)

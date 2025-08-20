package com.bachld.android.data.dto.response.giangvien

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

// Item trong content
data class DeTaiXetDuyetResponse(
    val maSV: String,
    val hoTen: String,
    val tenLop: String?,
    val soDienThoai: String?,
    val tenDeTai: String,
    val idDeTai: String,
    val trangThai: String,
    val tongQuanDeTaiUrl: String?
)


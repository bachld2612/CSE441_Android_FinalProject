package com.bachld.android.data.dto.response.hoidong

data class HoiDongListItemResponse(
    val id: Long,
    val tenHoiDong: String,
    val thoiGianBatDau: String,
    val thoiGianKetThuc: String,
    val loaiHoiDong: String?
)
package com.bachld.android.data.dto.response.decuong

data class DeCuongResponse(
    val id: Long?,
    val deCuongUrl: String?,
    val trangThai: DeCuongState?,
    val soLanNop: Int?,
    val nhanXet: String?,
    val tenDeTai: String?,
    val maSV: String?,
    val hoTenSinhVien: String?,
    val hoTenGiangVien: String?
)

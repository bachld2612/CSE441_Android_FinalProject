package com.bachld.android.data.dto.response.hoidong

data class HoiDongDetailResponse(
    val id: Long,
    val tenHoiDong: String,
    val thoiGianBatDau: String,
    val thoiGianKetThuc: String,
    val loaiHoiDong: String?,
    val chuTich: String?,
    val thuKy: String?,
    val giangVienPhanBien: List<String> = emptyList(),
    val sinhVienList: List<SinhVienTrongHoiDong> = emptyList()
) {
    data class SinhVienTrongHoiDong(
        val hoTen: String,
        val maSV: String,
        val lop: String,
        val tenDeTai: String,
        val gvhd: String,
        val boMon: String
    )
}

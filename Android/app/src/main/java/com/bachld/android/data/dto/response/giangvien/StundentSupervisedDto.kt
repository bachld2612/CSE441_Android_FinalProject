package com.bachld.android.data.dto.response.giangvien
import com.squareup.moshi.Json

data class StundentSupervisedDto(
    @Json(name = "maSV") val maSV: String,
    @Json(name = "hoTen") val hoTen: String,
    @Json(name = "tenLop") val tenLop: String,
    @Json(name = "soDienThoai") val soDienThoai: String?,
    @Json(name = "tenDeTai") val tenDeTai: String?,
    @Json(name = "cvUrl") val cvUrl: String? = null,
)

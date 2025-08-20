package com.bachld.android.data.dto.response.donhoandoan

import java.time.LocalDateTime

data class DonHoanDoAnResponse(
    val id: Long?,
    val sinhVienId: Long?,
    val trangThai: String?,           // HoanState (PENDING/APPROVED/REJECTED)
    val lyDo: String?,
    val minhChungUrl: String?,
    val requestedAt: LocalDateTime?,
    val decidedAt: LocalDateTime?,
    val nguoiPheDuyetId: Long?,
    val ghiChuQuyetDinh: String?
)

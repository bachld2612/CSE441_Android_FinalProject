package com.bachld.android.data.dto.response.decuong

data class DeCuongLogResponse(
    val fileUrlMoiNhat: String?,
    val ngayNopGanNhat: String?,       // yyyy-MM-dd từ LocalDate
    val tongSoLanNop: Int?,
    val trangThaiHienTai: DeCuongState?,
    val cacNhanXetTuChoi: List<RejectNote>?
) {
    data class RejectNote(
        val ngayNhanXet: String?,      // yyyy-MM-dd
        val lyDo: String?
    )
}

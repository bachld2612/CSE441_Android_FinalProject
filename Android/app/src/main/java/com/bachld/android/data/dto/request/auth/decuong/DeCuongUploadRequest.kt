package com.bachld.android.data.dto.request.decuong

import android.net.Uri

/**
 * Tên và field khớp backend:
 * - deTaiId: Long (bắt buộc)
 * - fileUrl: String? (tùy chọn)
 * - file (MultipartFile) -> phía Android đại diện bằng fileUri để repo chuyển sang MultipartBody.Part
 *
 * @Transient để không serialize khi dùng Retrofit (ta gửi multipart @Part riêng).
 */
data class DeCuongUploadRequest(
    val deTaiId: Long,
    val fileUrl: String? = null,
    @Transient val fileUri: Uri? = null
)

package com.bachld.android.data.dto.request.donhoandoan

import android.net.Uri

/**
 * Request dùng ở Android. `minhChungUri` chỉ để repo chuyển sang MultipartBody.Part.
 */
data class DonHoanDoAnRequest(
    val lyDo: String,
    @Transient val minhChungUri: Uri? = null
)

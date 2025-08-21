// app/src/main/java/com/bachld/android/core/LocalDateTimeJsonAdapter.kt
package com.bachld.android.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Adapter cho LocalDateTime với khả năng đọc chuỗi ISO có 'Z' hoặc offset (+07:00),
 * ví dụ: "2025-08-20T15:29:51.697Z" hoặc "2025-08-20T22:29:51.697+07:00".
 * Khi parse có offset, ta quy về UTC rồi trả LocalDateTime (mất thông tin múi giờ).
 * Khi serialize, xuất dạng ISO_OFFSET_DATE_TIME ở UTC (có 'Z') để khớp backend.
 */
class LocalDateTimeJsonAdapter {

    @RequiresApi(Build.VERSION_CODES.O)
    private val isoLocal = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    @RequiresApi(Build.VERSION_CODES.O)
    private val isoOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @FromJson
    fun fromJson(value: String?): LocalDateTime? {
        if (value == null) return null
        return try {
            // Ưu tiên parse dạng có offset/Z
            OffsetDateTime.parse(value, isoOffset)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime()
        } catch (_: Exception) {
            try {
                // Dạng thuần local không offset
                LocalDateTime.parse(value, isoLocal)
            } catch (_: Exception) {
                try {
                    // Fallback: epoch millis dưới dạng chuỗi
                    val epoch = value.toLong()
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC)
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ToJson
    fun toJson(value: LocalDateTime?): String? {
        if (value == null) return null
        // Chuẩn hoá xuất ra ISO_OFFSET_DATE_TIME (UTC) có 'Z' để đồng bộ với backend
        return value.atOffset(ZoneOffset.UTC).format(isoOffset)
    }
}

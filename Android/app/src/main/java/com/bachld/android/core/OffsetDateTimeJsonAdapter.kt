// app/src/main/java/com/bachld/android/core/OffsetDateTimeJsonAdapter.kt
package com.bachld.android.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeJsonAdapter {

    @RequiresApi(Build.VERSION_CODES.O)
    private val isoOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @FromJson
    fun fromJson(value: String?): OffsetDateTime? {
        if (value == null) return null
        return OffsetDateTime.parse(value, isoOffset)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ToJson
    fun toJson(value: OffsetDateTime?): String? {
        return value?.format(isoOffset)
    }
}

// app/src/main/java/com/bachld/android/core/MoshiDateTimeConfig.kt
package com.bachld.android.core

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiDateTimeConfig {

    fun build(): Moshi {
        return Moshi.Builder()
            // Thứ tự: các adapter thời gian trước, rồi tới KotlinJsonAdapterFactory
            .add(LocalDateJsonAdapter())
            .add(LocalDateTimeJsonAdapter())
            // .add(OffsetDateTimeJsonAdapter()) // mở nếu DTO dùng OffsetDateTime
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}

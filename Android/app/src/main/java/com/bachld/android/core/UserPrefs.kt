package com.bachld.android.core

import android.content.Context
import com.squareup.moshi.Moshi

import com.bachld.android.data.dto.response.auth.MyInfoResponse
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import androidx.core.content.edit

class UserPrefs(context: Context) {

    private val sp = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(MyInfoResponse::class.java)

    fun save(info: MyInfoResponse) {
        val json = adapter.toJson(info)
        sp.edit {
            putString(KEY_MY_INFO, json)
                .putLong(KEY_CACHED_AT, System.currentTimeMillis())
        }
    }

    fun getCached(): MyInfoResponse? {
        val json = sp.getString(KEY_MY_INFO, null) ?: return null
        return adapter.fromJson(json)
    }

    fun cachedAt(): Long = sp.getLong(KEY_CACHED_AT, 0L)

    fun isStale(maxAgeMillis: Long): Boolean {
        val saved = cachedAt()
        return saved == 0L || (System.currentTimeMillis() - saved) > maxAgeMillis
    }

    fun getRole(): String? = getCached()?.role

    fun clear() {
        sp.edit { remove(KEY_MY_INFO).remove(KEY_CACHED_AT) }
    }

    companion object {
        private const val KEY_MY_INFO = "my_info"
        private const val KEY_CACHED_AT = "my_info_cached_at"
        const val TTL_3H: Long = 3L * 60 * 60 * 1000
    }
}
package com.bachld.android.core

import android.content.Context
import android.content.Context.MODE_PRIVATE

class DeTaiIdCache(context: Context) {
    private val prefs = context.getSharedPreferences("de_tai_cache", MODE_PRIVATE)

    fun get(): Long? = prefs.getLong(KEY, -1L).takeIf { it > 0L }
    fun set(id: Long) { prefs.edit().putLong(KEY, id).apply() }
    fun clear() { prefs.edit().remove(KEY).apply() }

    private companion object { const val KEY = "de_tai_id" }
}

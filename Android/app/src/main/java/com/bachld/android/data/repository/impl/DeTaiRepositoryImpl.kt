package com.bachld.android.data.repository.impl

import android.util.Log
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.dto.response.unwrapOrThrow
import com.bachld.android.data.remote.service.DeTaiApi

class   DeTaiRepositoryImpl(
    private val api: DeTaiApi,
    private val prefs: UserPrefs
) {

    suspend fun getMyProject(forceRefresh: Boolean = false): DeTaiResponse? {
        val uid = prefs.getCached()?.id // có thể null nếu chưa lưu /me
        Log.d("TTDA", "uid=$uid, hasToken=${!prefs.getToken().isNullOrBlank()}")

        // chỉ dùng cache khi có uid
        if (uid != null && !forceRefresh) {
            val cached = prefs.getProject(uid)
            val cacheOk = cached != null && !prefs.isProjectStale(uid, UserPrefs.TTL_3H)
            if (cacheOk) return cached
        }

        // LUÔN gọi API (kể cả khi uid null)
        val remote = api.getMyDeTai().unwrapOrThrow()

        // nếu có uid thì mới lưu cache
        if (uid != null) {
            prefs.saveProject(uid, remote)
        }

        return remote
    }
}

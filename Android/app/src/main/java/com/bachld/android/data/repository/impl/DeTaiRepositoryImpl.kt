package com.bachld.android.data.repository.impl

import android.util.Log
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.response.detai.DeTaiResponse
import com.bachld.android.data.dto.response.unwrapOrThrow
import com.bachld.android.data.remote.service.DeTaiApi
import retrofit2.HttpException

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

        // Luôn thử gọi remote
        return try {
            val remote = api.getMyDeTai().unwrapOrThrow()
            if (uid != null) prefs.saveProject(uid, remote)
            remote
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Không có đề tài -> clear cache và trả null
                if (uid != null) prefs.clearProject(uid)  // tạo hàm này nếu chưa có
                null
            } else {
                // Có thể fallback cache khi lỗi khác (mạng…), hoặc ném lỗi
                if (uid != null) prefs.getProject(uid) ?: throw e
                else throw e
            }
        }
    }
}

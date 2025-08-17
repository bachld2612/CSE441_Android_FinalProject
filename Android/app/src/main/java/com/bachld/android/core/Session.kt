package com.bachld.android.core

import android.content.Context
import androidx.core.content.edit
import com.bachld.android.data.dto.request.auth.IntrospectRequest
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.data.repository.impl.AuthRepositoryImpl

object Session {

    private const val PREFS = "app_prefs"
    private lateinit  var appContext: Context

    private fun getAuthRepository(): AuthRepositoryImpl {
        return AuthRepositoryImpl(appContext)
    }
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    private const val KEY_TOKEN = "token"

    fun getContext(): Context {
        return appContext
    }

    fun getTokenSync(): String? {
        val sharedPreferences = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    suspend fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    suspend fun getToken(): String? {
        val sharedPreferences = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        val response = getAuthRepository().introspect(IntrospectRequest(token ?: ""))
        return if(response.code == 1000 && response.result?.valid == true) {
            token
        } else {
            null
        }
    }

    fun saveToken(token: String) {
        val sharedPreferences = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            if(token.isBlank()) remove(KEY_TOKEN) else putString(KEY_TOKEN, token)
        }
    }

    fun logout() {
        saveToken("")
    }

}
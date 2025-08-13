package com.bachld.android.core

import android.content.Context
import androidx.core.content.edit

object Session {

    private const val PREFS = "app_prefs"
    private const val KEY_TOKEN = "token"

    fun isLoggedIn(context: Context): Boolean = !getToken(context).isNullOrBlank()

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            if(token.isBlank()) remove(KEY_TOKEN) else putString(KEY_TOKEN, token)
        }
    }

    fun logout(context: Context) {
        saveToken(context, "")
    }

}
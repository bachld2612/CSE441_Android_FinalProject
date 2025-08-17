package com.bachld.android.core

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (private val appContext: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if(request.header("No-Auth") == "true") {
            val cleanRequest = request.newBuilder()
                .removeHeader("No-Auth")
                .build()
            return chain.proceed(cleanRequest)
        }
        val token = Session.getTokenSync()
        val newRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else request
        return chain.proceed(newRequest)
    }


}
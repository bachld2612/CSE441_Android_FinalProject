package com.bachld.android

import android.app.Application
import com.bachld.android.core.Session
import com.bachld.android.data.remote.client.ApiClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
        Session.init(this)
    }
}

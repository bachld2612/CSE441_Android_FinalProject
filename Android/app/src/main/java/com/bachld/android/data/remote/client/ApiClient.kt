// app/src/main/java/com/bachld/android/data/remote/client/ApiClient.kt
package com.bachld.android.data.remote.client

import android.app.Application
import com.bachld.android.core.ApiConfig
import com.bachld.android.core.AuthInterceptor
import com.bachld.android.core.MoshiDateTimeConfig
import com.bachld.android.data.remote.service.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var authApi: AuthApi
        private set
    lateinit var taiKhoanApi: TaiKhoanApi
        private set
    lateinit var app: Application
        private set
    lateinit var deTaiApi: DeTaiApi
        private set
    lateinit var deCuongApi: DeCuongApi
        private set
    lateinit var thongBaoApi: ThongBaoApi
    lateinit var sinhVienApi: SinhVienApi
    lateinit var donHoanDoAnApi: DonHoanDoAnApi
        private set

    fun init(app: Application) {
        this.app = app

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(app.applicationContext))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Dùng module cấu hình Moshi đã tách
        val moshi = MoshiDateTimeConfig.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        taiKhoanApi = retrofit.create(TaiKhoanApi::class.java)
        deTaiApi = retrofit.create(DeTaiApi::class.java)
        thongBaoApi = retrofit.create(ThongBaoApi::class.java)
        sinhVienApi = retrofit.create(SinhVienApi::class.java)
        deCuongApi = retrofit.create(DeCuongApi::class.java)
        donHoanDoAnApi = retrofit.create(DonHoanDoAnApi::class.java)
    }
}

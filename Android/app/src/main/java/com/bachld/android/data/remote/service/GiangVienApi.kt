package com.bachld.android.data.remote.service

import com.bachld.android.data.dto.request.giangvien.RejectDeTaiRequest
import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.detai.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.dto.response.giangvien.*
import retrofit2.http.*

interface GiangVienApi {

    @GET("/api/v1/giang-vien/do-an/xet-duyet-de-tai")
    suspend fun getXetDuyetDeTai(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): ApiResponse<PageData<DeTaiXetDuyetResponse>>

    @PUT("/api/v1/giang-vien/do-an/xet-duyet-de-tai/{id}/approve")
    suspend fun approveDeTai(
        @Path("id") idDeTai: Long
    ): ApiResponse<DeTaiResponse>       // ✅ trả DeTaiResponse

    @PUT("/api/v1/giang-vien/do-an/xet-duyet-de-tai/{id}/reject")
    suspend fun rejectDeTai(
        @Path("id") idDeTai: Long,
        @Body body: RejectDeTaiRequest
    ): ApiResponse<DeTaiResponse>


    @GET("/api/v1/giang-vien/list")
    suspend fun listGiangVien(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 200
    ): ApiResponse<PageData<GiangVienResponse>>

    @GET("/api/v1/giang-vien/sinh-vien/all") // nếu server không có context-path thì đổi thành "api/giang-vien/sinh-vien/all"
    suspend fun get_sinh_vien_huong_dan_all(
        @Query("q") q: String? = null
    ): ApiResponse<List<StundentSupervisedDto>>
}

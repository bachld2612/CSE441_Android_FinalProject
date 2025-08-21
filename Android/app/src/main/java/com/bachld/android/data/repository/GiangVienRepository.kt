package com.bachld.android.data.repository

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.dto.response.giangvien.DeTaiXetDuyetResponse
import com.bachld.android.data.dto.response.giangvien.PageData
import com.bachld.android.data.model.SupervisedStudent

interface GiangVienRepository {

    suspend fun fetchDeTai(page: Int, size: Int): ApiResponse<PageData<DeTaiXetDuyetResponse>>
    suspend fun approveDeTai(idDeTai: Long): ApiResponse<DeTaiResponse>
    suspend fun rejectDeTai(idDeTai: Long, lyDo: String): ApiResponse<DeTaiResponse>
    suspend fun get_sinh_vien_huong_dan_all(q: String? = null): List<SupervisedStudent>

}
package com.bachld.android.data.repository.impl

import com.bachld.android.data.dto.response.ApiResponse
import com.bachld.android.data.dto.response.thongbao.PageResponse
import com.bachld.android.data.dto.response.thongbao.ThongBaoResponse
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.remote.service.ThongBaoApi
import com.bachld.android.data.repository.ThongBaoRepository

class ThongBaoRepositoryImpl: ThongBaoRepository{

    private val thongBaoApi = ApiClient.thongBaoApi

    override suspend fun getThongBao(): ApiResponse<PageResponse<ThongBaoResponse>> {

        return thongBaoApi.getThongBao()

    }

    override suspend fun getThongBaoDetail(id: Long): ApiResponse<ThongBaoResponse> {
        return thongBaoApi.getThongBaoDetail(id)
    }

}
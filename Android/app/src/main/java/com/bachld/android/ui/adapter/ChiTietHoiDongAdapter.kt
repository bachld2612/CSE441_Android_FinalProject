package com.bachld.android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.data.dto.response.hoidong.HoiDongDetailResponse
import com.bachld.android.databinding.ItemChiTietHoiDongBinding

class ChiTietHoiDongAdapter :
    RecyclerView.Adapter<ChiTietHoiDongAdapter.VH>() {

    private val data = mutableListOf<HoiDongDetailResponse.SinhVienTrongHoiDong>()

    fun submit(list: List<HoiDongDetailResponse.SinhVienTrongHoiDong>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val vb: ItemChiTietHoiDongBinding) : RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemChiTietHoiDongBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(vb)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = data[pos]
        h.vb.tvNameDetail.text = s.hoTen
        h.vb.tvTopicDetail.text = s.tenDeTai
        h.vb.tvMsvDetail.text = s.maSV
        h.vb.tvSupervisorDetail.text = s.gvhd
        h.vb.tvClassDetail.text = s.lop
        h.vb.tvDeptDetail.text = s.boMon
    }

    override fun getItemCount() = data.size
}

package com.bachld.android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.data.dto.response.hoidong.HoiDongListItemResponse
import com.bachld.android.databinding.ItemHoiDongBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HoiDongAdapter(
    private val onMoreClick: (HoiDongListItemResponse) -> Unit
) : RecyclerView.Adapter<HoiDongAdapter.VH>() {

    private val data = mutableListOf<HoiDongListItemResponse>()

    // Dùng java.text -> hỗ trợ từ API 1
    private val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outFmt = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))

    fun submit(list: List<HoiDongListItemResponse>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val vb: ItemHoiDongBinding) : RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemHoiDongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = data[pos]
        h.vb.txtBoardName.text = item.tenHoiDong

        fun reformat(s: String): String =
            runCatching { outFmt.format(inFmt.parse(s)!!) }.getOrElse { s }

        val from = reformat(item.thoiGianBatDau)
        val to   = reformat(item.thoiGianKetThuc)
        h.vb.textView4.text = "$from – $to"

        h.vb.tvMore.setOnClickListener { onMoreClick(item) }
        h.itemView.setOnClickListener { onMoreClick(item) }
    }

    override fun getItemCount() = data.size
}

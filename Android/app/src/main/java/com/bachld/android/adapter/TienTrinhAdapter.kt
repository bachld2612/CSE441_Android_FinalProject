package com.bachld.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.databinding.ItemTienTrinhBinding
import com.bachld.android.model.TienTrinhDoAn

class TienTrinhAdapter(private val list: List<TienTrinhDoAn>)
    : RecyclerView.Adapter<TienTrinhAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTienTrinhBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTienTrinhBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvNoiDung.text = item.noiDung
        holder.binding.tvThoiGian.text = if (item.thoiGianBatDau == item.thoiGianKetThuc)
            item.thoiGianBatDau
        else
            "${item.thoiGianBatDau} - ${item.thoiGianKetThuc}"

        // Đổi màu nền LinearLayout chính
        val layout = holder.binding.layoutTienTrinh // <-- ID LinearLayout trong XML
        val bgColor = if (item.isHighlight)
            ContextCompat.getColor(holder.itemView.context, R.color.light_yellow)
        else
            ContextCompat.getColor(holder.itemView.context, android.R.color.white)
        layout.setBackgroundColor(bgColor)
    }

    override fun getItemCount() = list.size
}

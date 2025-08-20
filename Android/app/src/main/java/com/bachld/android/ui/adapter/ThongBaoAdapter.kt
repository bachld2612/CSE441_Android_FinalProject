package com.bachld.android.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.data.dto.response.thongbao.ThongBaoResponse
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ThongBaoAdapter(
    private val onItemClick: (ThongBaoResponse) -> Unit
) : ListAdapter<ThongBaoResponse, ThongBaoAdapter.ThongBaoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThongBaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thong_bao, parent, false)
        return ThongBaoViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ThongBaoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ThongBaoViewHolder(
        itemView: View,
        private val onItemClick: (ThongBaoResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgIcon: ImageView = itemView.findViewById(R.id.img_icon)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

        private val displayFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")

        fun bind(item: ThongBaoResponse) {
            tvTitle.text = item.tieuDe

            tvDate.text = try {
                item.createdAt.format(displayFormatter)
            } catch (e: Exception) {
                item.createdAt.toString()
            }

            if (!item.fileUrl.isNullOrBlank()) {
                imgIcon.visibility = View.VISIBLE
                imgIcon.setImageResource(R.drawable.ic_file)
            } else {
                imgIcon.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ThongBaoResponse>() {
        override fun areItemsTheSame(
            oldItem: ThongBaoResponse,
            newItem: ThongBaoResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ThongBaoResponse,
            newItem: ThongBaoResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}

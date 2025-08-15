package com.bachld.android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.databinding.ItemProfileRowBinding

data class ProfileRow(val label: String, val value: String)

class ProfileAdapter(
    private val items: MutableList<ProfileRow> = mutableListOf()
) : RecyclerView.Adapter<ProfileAdapter.VH>() {

    class VH(val binding: ItemProfileRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemProfileRowBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val (label, value) = items[position]
        holder.binding.tvLabel.text = label
        holder.binding.tvValue.text = value
    }

    override fun getItemCount() = items.size

    fun submit(list: List<ProfileRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}

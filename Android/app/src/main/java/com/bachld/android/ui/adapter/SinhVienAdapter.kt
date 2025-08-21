package com.bachld.android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.data.model.SupervisedStudent

class SinhVienAdapter :
    ListAdapter<SupervisedStudent, SinhVienAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SupervisedStudent>() {
            override fun areItemsTheSame(o: SupervisedStudent, n: SupervisedStudent) =
                o.maSV == n.maSV
            override fun areContentsTheSame(o: SupervisedStudent, n: SupervisedStudent) =
                o == n
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv_value_msv = view.findViewById<TextView>(R.id.tv_value_msv)
        private val tv_value_ho_ten = view.findViewById<TextView>(R.id.tv_value_ho_ten)
        private val tv_value_lop = view.findViewById<TextView>(R.id.tv_value_lop)
        private val tv_value_sdt = view.findViewById<TextView>(R.id.tv_value_sdt)
        fun bind(item: SupervisedStudent) {
            tv_value_msv.text = item.maSV
            tv_value_ho_ten.text = item.hoTen
            tv_value_lop.text = item.tenLop
            tv_value_sdt.text = item.soDienThoai ?: "—"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sinh_vien, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}

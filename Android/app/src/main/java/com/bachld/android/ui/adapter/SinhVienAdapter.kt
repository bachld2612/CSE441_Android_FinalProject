package com.bachld.android.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.data.model.SupervisedStudent

class SinhVienAdapter(
    private val onOpenCv: (String) -> Unit,     // ← callback mở CV
    private val onDownloadCv: (String, String) -> Unit = { _, _ -> } // nhấn giữ để tải (tuỳ chọn)
) : ListAdapter<SupervisedStudent, SinhVienAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SupervisedStudent>() {
            override fun areItemsTheSame(o: SupervisedStudent, n: SupervisedStudent) = o.maSV == n.maSV
            override fun areContentsTheSame(o: SupervisedStudent, n: SupervisedStudent) = o == n
        }
    }

    init { setHasStableIds(true) }
    override fun getItemId(position: Int): Long = getItem(position).maSV.hashCode().toLong()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvValueMsv = view.findViewById<TextView>(R.id.tv_value_msv)
        private val tvValueHoTen = view.findViewById<TextView>(R.id.tv_value_ho_ten)
        private val tvValueLop = view.findViewById<TextView>(R.id.tv_value_lop)
        private val tvValueSdt = view.findViewById<TextView>(R.id.tv_value_sdt)
        private val tvValueDeTai = view.findViewById<TextView>(R.id.tv_value_de_tai)
        private val tvValueCvUrl = view.findViewById<TextView>(R.id.tv_value_cv_url)

        fun bind(item: SupervisedStudent) {
            tvValueMsv.text = item.maSV
            tvValueHoTen.text = item.hoTen
            tvValueLop.text = item.tenLop
            tvValueSdt.text = item.soDienThoai ?: "—"

            tvValueDeTai.text = item.tenDeTai?.takeIf { it.isNotBlank() } ?: "Không có"

            val cv = item.cvUrl?.trim()
            if (cv.isNullOrEmpty()) {
                tvValueCvUrl.apply {
                    text = "Không có"
                    isEnabled = false
                    alpha = 0.8f
                    paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    setOnClickListener(null)
                    setOnLongClickListener(null)
                }
            } else {
                tvValueCvUrl.apply {
                    text = "Xem CV"
                    isEnabled = true
                    alpha = 1f
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    setOnClickListener { onOpenCv(cv) }
                    setOnLongClickListener {
                        onDownloadCv(cv, "${item.maSV}-CV.pdf")
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_sinh_vien, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}

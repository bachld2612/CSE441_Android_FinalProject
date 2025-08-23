package com.bachld.android.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.data.dto.response.decuong.DeCuongItem
import com.bachld.android.databinding.ItemDeCuongGvBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeCuongGvAdapter(
    private val onApprove: (id: Long) -> Unit,
    private val onReject: (id: Long, reason: String) -> Unit
) : ListAdapter<DeCuongItem, DeCuongGvAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DeCuongItem>() {
            override fun areItemsTheSame(o: DeCuongItem, n: DeCuongItem) = o.id == n.id
            override fun areContentsTheSame(o: DeCuongItem, n: DeCuongItem) = o == n
        }
    }

    inner class VH(val vb: ItemDeCuongGvBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(item: DeCuongItem) = with(vb) {
            tvTopic.text   = "Đề tài: ${item.topicTitle}"
            tvMaSV.text    = "Mã sinh viên: ${item.studentCode}"
            tvHoTenSV.text = "Họ và tên: ${item.studentName}"
            tvHoTenGV.text = "GV hướng dẫn: ${item.teacherName}"

            // link
            tvLink.text = if (!item.url.isNullOrEmpty()) {
                "Đề cương sinh viên"
            } else {
                "Không có"
            }
            tvLink.setOnClickListener {
                item.url?.let { openUrl(root.context, it) }
            }

            val status = item.status.uppercase()
            val (label, colorRes) = when (status) {
                "ACCEPTED"  -> "đã đồng ý"     to R.color.state_success
                "CANCELED" -> "đã từ chối" to R.color.state_reject
                else        -> "chờ xét duyệt" to R.color.state_pending
            }
            chipStatus.text = label
            chipStatus.setChipBackgroundColorResource(colorRes)
            chipStatus.setTextColor(ContextCompat.getColor(root.context, R.color.black))

            val isPending = status == "PENDING"
            layoutActions.visibility = if (isPending) View.VISIBLE else View.GONE

            btnApprove.setOnClickListener { showApproveDialog(root.context, item.id) }
            btnReject.setOnClickListener  { showRejectDialog(root.context, item.id) }
        }

        private fun openUrl(ctx: Context, url: String) {
            runCatching {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        private fun showApproveDialog(ctx: Context, id: Long) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Duyệt đề cương")
                .setMessage("Xác nhận duyệt đề cương #$id ?")
                .setNegativeButton("Trở về", null)
                .setPositiveButton("Xác nhận") { _, _ -> onApprove(id) }
                .show()
        }

        private fun showRejectDialog(ctx: Context, id: Long) {
            val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reject_reason, null, false)
            val dialog = MaterialAlertDialogBuilder(ctx).setView(view).create()
            val btnCancel  = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            val btnConfirm = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnConfirmReject)
            val edtReason  = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtReason)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                val reason = edtReason.text?.toString()?.trim().orEmpty()
                if (reason.isEmpty()) { edtReason.error = "Vui lòng nhập lý do"; return@setOnClickListener }
                dialog.dismiss(); onReject(id, reason)
            }
            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemDeCuongGvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}

package com.bachld.android.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.data.dto.response.giangvien.DeTaiXetDuyetResponse
import com.bachld.android.databinding.ItemXetDuyetDetaiBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class XetDuyetDeTaiAdapter(
    private val onApprove: (idDeTai: Long) -> Unit,
    private val onReject: (idDeTai: Long, reason: String) -> Unit
) : ListAdapter<DeTaiXetDuyetResponse, XetDuyetDeTaiAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DeTaiXetDuyetResponse>() {
            override fun areItemsTheSame(o: DeTaiXetDuyetResponse, n: DeTaiXetDuyetResponse) = o.idDeTai == n.idDeTai
            override fun areContentsTheSame(o: DeTaiXetDuyetResponse, n: DeTaiXetDuyetResponse) = o == n
        }
    }

    inner class VH(val vb: ItemXetDuyetDetaiBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(item: DeTaiXetDuyetResponse) = with(vb) {
            tvTenDeTai.text = "Đề tài: ${item.tenDeTai}"
            tvMaSV.text     = "Mã sinh viên: ${item.maSV}"
            tvHoTen.text    = "Họ và tên: ${item.hoTen}"
            tvTenLop.text   = "Lớp: ${item.tenLop ?: "-"}"

            val status = item.trangThai.uppercase()
            val (label, colorRes) = when (status) {
                "ACCEPTED"  -> "đã đồng ý"     to R.color.state_success
                "CANCELED" -> "đã từ chối" to R.color.state_reject
                else        -> "chờ xét duyệt" to R.color.state_pending
            }
            chipTrangThai.text = label
            chipTrangThai.setChipBackgroundColorResource(colorRes)
            chipTrangThai.setTextColor(ContextCompat.getColor(root.context, R.color.black))

            val isPending = status == "PENDING"
            layoutAction.visibility = if (isPending) View.VISIBLE else View.GONE

            btnApprove.setOnClickListener { showApproveDialog(root.context, item) }
            btnReject.setOnClickListener  { showRejectDialog(root.context, item) }
        }

        private fun idAsLong(item: DeTaiXetDuyetResponse): Long =
            item.idDeTai.toLongOrNull() ?: 0L

        private fun showApproveDialog(ctx: Context, item: DeTaiXetDuyetResponse) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Duyệt đề tài")
                .setMessage("Xác nhận duyệt đề tài cho ${item.hoTen}?")
                .setNegativeButton("Trở về", null)
                .setPositiveButton("Xác nhận") { _, _ -> onApprove(idAsLong(item)) }
                .show()
        }

        private fun showRejectDialog(ctx: Context, item: DeTaiXetDuyetResponse) {
            val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reject_reason, null, false)
            val dialog = MaterialAlertDialogBuilder(ctx).setView(view).create()
            val btnCancel  = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            val btnConfirm = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnConfirmReject)
            val edtReason  = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtReason)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                val reason = edtReason.text?.toString()?.trim().orEmpty()
                if (reason.isEmpty()) { edtReason.error = "Vui lòng nhập lý do"; return@setOnClickListener }
                dialog.dismiss(); onReject(idAsLong(item), reason)
            }
            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vb = ItemXetDuyetDetaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(vb)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}

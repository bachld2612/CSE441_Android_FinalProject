package com.bachld.android.ui.adapter

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bachld.android.R
import com.bachld.android.core.ApiConfig
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

        /** Ghép BASE_URL nếu backend trả đường dẫn tương đối */
        private fun resolve_url(raw: String): String {
            return if (raw.startsWith("http://") || raw.startsWith("https://")) raw
            else ApiConfig.BASE_URL.trimEnd('/') + "/" + raw.trimStart('/')
        }

        /** Mở bằng Google Docs Viewer để xem PDF (kể cả URL không có .pdf) */
        private fun open_in_viewer(ctx: Context, originalUrl: String) {
            val gview = "https://docs.google.com/gview?embedded=1&url=" + Uri.encode(originalUrl)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gview)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                ctx.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                // fallback: mở thẳng URL gốc bằng trình duyệt
                try {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(originalUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } catch (_: Exception) { /* no-op */ }
            }
        }

        /** Tải về bằng DownloadManager, đặt tên .pdf trong thư mục Downloads */
        private fun enqueue_download(ctx: Context, url: String, fileNamePdf: String) {
            val req = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileNamePdf)
                .setMimeType("application/pdf")
                .addRequestHeader("Accept", "application/pdf")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileNamePdf
                )

            val dm = ctx.getSystemService(DownloadManager::class.java)
            dm.enqueue(req)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long =
        getItem(position).maSV.hashCode().toLong()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv_value_msv = view.findViewById<TextView>(R.id.tv_value_msv)
        private val tv_value_ho_ten = view.findViewById<TextView>(R.id.tv_value_ho_ten)
        private val tv_value_lop = view.findViewById<TextView>(R.id.tv_value_lop)
        private val tv_value_sdt = view.findViewById<TextView>(R.id.tv_value_sdt)
        private val tv_value_de_tai = view.findViewById<TextView>(R.id.tv_value_de_tai)
        private val tv_value_cv_url = view.findViewById<TextView>(R.id.tv_value_cv_url)

        fun bind(item: SupervisedStudent) {
            // Trường cơ bản
            tv_value_msv.text = item.maSV
            tv_value_ho_ten.text = item.hoTen
            tv_value_lop.text = item.tenLop
            tv_value_sdt.text = item.soDienThoai ?: "—"

            // Đề tài: nếu rỗng/null → "Không có"
            tv_value_de_tai.text = item.tenDeTai?.takeIf { it.isNotBlank() } ?: "Không có"

            // CV
            val cv = item.cvUrl?.trim()
            if (cv.isNullOrEmpty()) {
                tv_value_cv_url.apply {
                    text = "Không có"
                    isEnabled = false
                    alpha = 0.8f
                    paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    setOnClickListener(null)
                    setOnLongClickListener(null)
                }
            } else {
                tv_value_cv_url.apply {
                    text = "Xem CV"
                    isEnabled = true
                    alpha = 1f
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    // Bấm: mở xem bằng Google Docs Viewer
                    setOnClickListener {
                        val finalUrl = resolve_url(cv)
                        open_in_viewer(context, finalUrl)
                    }

                    // Nhấn & giữ: tải về Downloads/<maSV>-CV.pdf
                    setOnLongClickListener {
                        val finalUrl = resolve_url(cv)
                        val fileName = "${item.maSV}-CV.pdf"
                        try {
                            enqueue_download(context, finalUrl, fileName)
                            // Có thể Toast "Đang tải..." nếu muốn
                        } catch (_: Exception) { /* no-op */ }
                        true
                    }
                }
            }
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

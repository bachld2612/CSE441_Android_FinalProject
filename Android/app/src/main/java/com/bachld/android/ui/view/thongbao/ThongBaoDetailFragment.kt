package com.bachld.android.ui.view.thongbao

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bachld.android.R
import androidx.core.net.toUri

class ThongBaoDetailFragment : Fragment(R.layout.fragment_thong_bao_detail) {
    private val args: ThongBaoDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvDownloadLink = view.findViewById<TextView>(R.id.tvDownloadLink)

        tvTitle.text = args.title
        tvContent.text = args.content
        tvDate.text = args.createdAt

        if (!args.fileUrl.isNullOrBlank()) {
            tvDownloadLink.visibility = View.VISIBLE
            tvDownloadLink.setOnClickListener { startDownload(args.fileUrl!!, args.title) }
        } else {
            tvDownloadLink.visibility = View.GONE
        }
    }

    private fun startDownload(url: String, fileName: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(fileName)
            .setDescription("Đang tải xuống...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        val dm = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}

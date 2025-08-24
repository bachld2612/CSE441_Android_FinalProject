package com.bachld.android.ui.view.thongbao

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bachld.android.R
import com.bachld.android.databinding.FragmentThongBaoDetailBinding
import java.net.URLEncoder
import java.util.Locale

class ThongBaoDetailFragment : Fragment(R.layout.fragment_thong_bao_detail) {

    private val args: ThongBaoDetailFragmentArgs by navArgs()

    private var _binding: FragmentThongBaoDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentThongBaoDetailBinding.bind(view)

        binding.tvTitle.text = args.title
        binding.tvContent.text = args.content
        binding.tvDate.text = args.createdAt

        if (!args.fileUrl.isNullOrBlank()) {
            binding.tvDownloadLink.visibility = View.VISIBLE
            binding.tvDownloadLink.setOnClickListener {
                openInOnlineViewerOrBrowser(args.fileUrl!!, args.title)
            }
        } else {
            binding.tvDownloadLink.visibility = View.GONE
        }
    }

    private fun openInOnlineViewerOrBrowser(url: String, title: String) {
        val ext = getExt(url) ?: getExt(title)
        val viewerUrl = buildViewerUrl(url, ext)

        try {
            CustomTabsIntent.Builder().build().launchUrl(requireContext(), viewerUrl.toUri())
            return
        } catch (_: Throwable) {}

        val viewIntent = Intent(Intent.ACTION_VIEW, viewerUrl.toUri())
        try {
            startActivity(viewIntent)
            return
        } catch (_: ActivityNotFoundException) {}

        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            return
        } catch (_: ActivityNotFoundException) {}

        startDownload(url, title)
        Toast.makeText(requireContext(), "Không có app để mở, đang tải về…", Toast.LENGTH_SHORT).show()
    }

    private fun buildViewerUrl(url: String, ext: String?): String {
        val encoded = URLEncoder.encode(url, "UTF-8")
        return when (ext?.lowercase(Locale.ROOT)) {
            "pdf" -> "https://docs.google.com/viewer?embedded=true&url=$encoded"
            "doc", "docx", "xls", "xlsx", "ppt", "pptx" ->
                "https://view.officeapps.live.com/op/view.aspx?src=$encoded"
            else ->
                "https://docs.google.com/viewer?embedded=true&url=$encoded"
        }
    }

    private fun getExt(nameOrUrl: String?): String? {
        if (nameOrUrl.isNullOrBlank()) return null
        val clean = nameOrUrl.substringBeforeLast('#').substringBeforeLast('?')
        val dot = clean.lastIndexOf('.')
        return if (dot != -1 && dot < clean.length - 1) clean.substring(dot + 1) else null
    }

    private fun startDownload(url: String, fileName: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(fileName)
            .setDescription("Đang tải xuống…")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        val dm = requireContext().getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

package com.bachld.android.ui.view.giangvien

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bachld.android.R
import com.bachld.android.core.ApiConfig

class CvWebViewFragment : Fragment(R.layout.fragment_cv_webview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val web = view.findViewById<WebView>(R.id.wv)
        val pb  = view.findViewById<ProgressBar>(R.id.pb)

        val raw = requireArguments().getString(ARG_CV_URL)
            ?: error("Missing arg: $ARG_CV_URL")

        // Nếu CV trả đường dẫn tương đối thì ghép BASE_URL
        val finalUrl = if (raw.startsWith("http://") || raw.startsWith("https://"))
            raw
        else
            ApiConfig.BASE_URL.trimEnd('/') + "/" + raw.trimStart('/')

        // Google Docs Viewer: ăn được link public (kể cả không .pdf)
        val gviewUrl = "https://docs.google.com/gview?embedded=1&url=" + Uri.encode(finalUrl)

        // Progress theo phần trăm thật
        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                // Khi >=95% coi như đã render xong (tránh kẹt 99%)
                pb.isVisible = newProgress < 95
            }
        }

        // Ẩn spinner khi nội dung thật sự được vẽ
        web.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.toString()?.let { view?.loadUrl(it) }
                return true
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                pb.isVisible = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                pb.isVisible = false // fallback
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                pb.isVisible = false
            }

            override fun onReceivedHttpError(
                view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
            ) {
                pb.isVisible = false
            }
        }

        // Cho phép zoom, DOM storage, viewport… để gview hiển thị ổn định
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        // Nếu trang gợi ý tải xuống (Content-Disposition), cho phép DownloadManager xử lý
        web.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            val guessed = URLUtil.guessFileName(url, contentDisposition, mimeType)
            val req = DownloadManager.Request(Uri.parse(url))
                .setTitle(guessed)
                .setMimeType(mimeType)
                .addRequestHeader("User-Agent", userAgent)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, guessed)

            val dm = requireContext().getSystemService(DownloadManager::class.java)
            dm.enqueue(req)
        }

        // Time-out an toàn: nếu vì lý do hiếm khi progress không lên 95%
        web.postDelayed({ pb.isVisible = false }, 4000)

        // Bắt đầu tải
        pb.isVisible = true
        web.loadUrl(gviewUrl)
    }

    override fun onPause() {
        view?.findViewById<WebView>(R.id.wv)?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<WebView>(R.id.wv)?.onResume()
    }

    override fun onDestroyView() {
        // Dọn WebView tránh leak
        val web = view?.findViewById<WebView>(R.id.wv) ?: return
        web.apply {
            stopLoading()
            // KHÔNG gán null – gán client rỗng để “tháo” callback
            webChromeClient = object : WebChromeClient() {}
            webViewClient = object : WebViewClient() {}
            loadUrl("about:blank")
            clearHistory()
            removeAllViews()
            // Tách khỏi parent rồi destroy
            (parent as? ViewGroup)?.removeView(this)
            destroy()
        }
        super.onDestroyView()
    }

    companion object {
        const val ARG_CV_URL = "cv_url"
    }
}

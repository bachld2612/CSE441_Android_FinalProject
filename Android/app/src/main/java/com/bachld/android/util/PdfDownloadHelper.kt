package com.bachld.android.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object PdfDownloadHelper {
    fun enqueue_download(ctx: Context, url: String, fileName: String = "cv.pdf") {
        val req = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setMimeType("application/pdf")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            // một số server cần header này để chọn đúng định dạng
            .addRequestHeader("Accept", "application/pdf")

        val dm = ctx.getSystemService(DownloadManager::class.java)
        dm.enqueue(req)
    }
}

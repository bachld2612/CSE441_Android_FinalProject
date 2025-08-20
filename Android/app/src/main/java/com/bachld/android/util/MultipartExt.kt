package com.bachld.android.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.source

/** Tạo RequestBody text/plain từ String */
fun String.toPlainRequestBody(): RequestBody =
    this.toRequestBody("text/plain".toMediaType())

/** Lấy tên hiển thị của file từ Uri */
private fun getDisplayName(context: Context, uri: Uri): String {
    var name = "upload"
    val cr: ContentResolver = context.contentResolver
    val cursor: Cursor? = cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && idx >= 0) name = it.getString(idx) ?: name
    }
    return name
}

/** Chuyển Uri (SAF) thành MultipartBody.Part mà không cần file tạm */
fun uriToMultipart(context: Context, uri: Uri, partName: String = "file"): MultipartBody.Part {
    val cr = context.contentResolver
    val mime = cr.getType(uri) ?: "application/octet-stream"
    val displayName = getDisplayName(context, uri)

    val body = object : RequestBody() {
        override fun contentType() = mime.toMediaTypeOrNull()
        override fun writeTo(sink: okio.BufferedSink) {
            cr.openInputStream(uri)?.use { input ->
                sink.writeAll(input.source())
            } ?: error("Không đọc được dữ liệu từ Uri: $uri")
        }
    }
    return MultipartBody.Part.createFormData(partName, displayName, body)
}

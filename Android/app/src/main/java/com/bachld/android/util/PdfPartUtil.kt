package com.bachld.android.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

object PdfPartUtil {

    fun createPdfPart(context: Context, uri: Uri, fieldName: String = "file"): MultipartBody.Part {
        val cr = context.contentResolver

        val mime = cr.getType(uri) ?: "application/octet-stream"
        require(mime == "application/pdf") { "Chỉ chấp nhận PDF" }

        // Lấy tên + size để tự kiểm tra rule backend (<= 10MB)
        var name = "cv.pdf"
        var size: Long? = null
        cr.query(uri, null, null, null, null)?.use { c ->
            val idxName = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val idxSize = c.getColumnIndex(OpenableColumns.SIZE)
            if (c.moveToFirst()) {
                if (idxName != -1) name = c.getString(idxName) ?: name
                if (idxSize != -1 && !c.isNull(idxSize)) size = c.getLong(idxSize)
            }
        }
        require(size == null || size!! <= 10L * 1024 * 1024) { "File vượt quá 10MB" }

        val body = object : RequestBody() {
            override fun contentType() = "application/pdf".toMediaType()
            override fun writeTo(sink: BufferedSink) {
                cr.openInputStream(uri)?.use { input ->
                    sink.writeAll(input.source())
                }
            }
        }

        return MultipartBody.Part.createFormData(fieldName, name, body)
    }
}
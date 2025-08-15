package com.bachld.android.ui.view.thongtin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

suspend fun compressJpegFromUri(
    ctx: Context,
    uri: Uri,
    maxW: Int = 1024,
    maxH: Int = 1024,
    quality: Int = 85
): ByteArray = withContext(Dispatchers.IO) {
    ctx.contentResolver.openInputStream(uri)!!.use { input ->
        val original = BitmapFactory.decodeStream(input)

        val ratio = minOf(
            maxW.toFloat() / original.width,
            maxH.toFloat() / original.height,
            1f
        )
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * ratio).toInt(),
                (original.height * ratio).toInt(),
                true
            )
        } else original

        ByteArrayOutputStream().use { baos ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            baos.toByteArray()
        }
    }
}

fun buildCompressedPart(bytes: ByteArray): MultipartBody.Part {
    val body = bytes.toRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData("file", "anh_dai_dien.jpg", body)
}

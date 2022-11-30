package com.kaiwolfram.nozzle.data

import android.content.Context
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest

class PictureRequester(
    private val imageLoader: ImageLoader,
    private val context: Context
) {
    suspend fun request(url: String): Painter? {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowConversionToBitmap(true)
            .build()
        val result = imageLoader.execute(request).drawable?.toBitmap()?.asImageBitmap()
        return if (result != null) BitmapPainter(result) else null
    }
}

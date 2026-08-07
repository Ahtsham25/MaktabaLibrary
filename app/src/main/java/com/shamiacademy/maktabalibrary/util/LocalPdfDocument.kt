package com.shamiacademy.maktabalibrary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Wraps android.graphics.pdf.PdfRenderer for a single local file so pages
 * can be rendered on demand as the user swipes, without loading the whole
 * document into memory at once. PdfRenderer is not safe for concurrent
 * access, so all calls are serialized through a Mutex.
 */
class LocalPdfDocument(private val context: Context, private val path: String) {

    private var pfd: ParcelFileDescriptor? = null
    private var renderer: PdfRenderer? = null
    private val mutex = Mutex()

    suspend fun open(): Int = withContext(Dispatchers.IO) {
        mutex.withLock {
            val file = File(path)
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd!!)
            renderer?.pageCount ?: 0
        }
    }

    suspend fun renderPage(index: Int, targetWidthPx: Int): Bitmap? = withContext(Dispatchers.IO) {
        mutex.withLock {
            val r = renderer ?: return@withLock null
            if (index < 0 || index >= r.pageCount) return@withLock null
            try {
                r.openPage(index).use { page ->
                    val ratio = page.height.toFloat() / page.width.toFloat()
                    val h = (targetWidthPx * ratio).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(targetWidthPx, h, Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        mutex.withLock {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            renderer = null
            pfd = null
        }
    }
}

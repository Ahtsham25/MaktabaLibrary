package com.shamiacademy.maktabalibrary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Renders the first page of a locally-downloaded PDF into a bitmap and
 * caches it on disk so it only needs to be rendered once.
 *
 * NOTE: This only works for PDFs already saved locally (i.e. downloaded
 * books). For books not yet downloaded, the maktaba's shared cover image
 * is shown instead until the book is downloaded, since Android's
 * PdfRenderer needs full random file access and cannot render from a
 * partial network stream.
 */
object PdfThumbnailUtil {

    private fun cacheKey(localPath: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(localPath.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    suspend fun getThumbnail(context: Context, localPdfPath: String, widthPx: Int = 300): Bitmap? =
        withContext(Dispatchers.IO) {
            val cacheDir = File(context.cacheDir, "pdf_thumbs").apply { mkdirs() }
            val cacheFile = File(cacheDir, cacheKey(localPdfPath) + ".png")

            if (cacheFile.exists()) {
                return@withContext android.graphics.BitmapFactory.decodeFile(cacheFile.absolutePath)
            }

            try {
                val pdfFile = File(localPdfPath)
                if (!pdfFile.exists()) return@withContext null

                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                    PdfRenderer(pfd).use { renderer ->
                        if (renderer.pageCount == 0) return@withContext null
                        renderer.openPage(0).use { page ->
                            val ratio = page.height.toFloat() / page.width.toFloat()
                            val h = (widthPx * ratio).toInt()
                            val bitmap = Bitmap.createBitmap(widthPx, h, Bitmap.Config.ARGB_8888)
                            bitmap.eraseColor(Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                            FileOutputStream(cacheFile).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                            }
                            return@withContext bitmap
                        }
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
}

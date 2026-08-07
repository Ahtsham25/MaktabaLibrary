package com.shamiacademy.maktabalibrary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Produces each book's own title-page thumbnail reliably.
 *
 * Android's PdfRenderer can only open a *complete* local PDF file (it needs
 * random access to the whole document structure), so there is no safe way
 * to render page 1 from a partial network stream. This fetches the PDF
 * fully into a temporary file, renders just page 1, saves that image
 * permanently to a small on-disk cache, and immediately deletes the
 * temporary PDF — so the one-time network cost only happens once per book,
 * ever, and no extra storage is kept beyond the small thumbnail image.
 *
 * Concurrency is capped (a few at a time) so scrolling quickly through the
 * grid doesn't try to download dozens of PDFs simultaneously.
 */
object TempThumbnailUtil {

    private val client = OkHttpClient()
    private val semaphore = Semaphore(2)

    private fun cacheFile(context: Context, pdfUrl: String): File {
        val dir = File(context.cacheDir, "book_thumbs").apply { mkdirs() }
        val key = MessageDigest.getInstance("MD5").digest(pdfUrl.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(dir, "$key.jpg")
    }

    suspend fun getThumbnail(context: Context, pdfUrl: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val cached = cacheFile(context, pdfUrl)
            if (cached.exists()) {
                return@withContext BitmapFactory.decodeFile(cached.absolutePath)
            }

            semaphore.withPermit {
                // Check again — another job may have finished this exact
                // book while we were waiting for a permit.
                if (cached.exists()) {
                    return@withPermit BitmapFactory.decodeFile(cached.absolutePath)
                }

                val tempFile = File(context.cacheDir, "tmp_${cached.name}.pdf")
                try {
                    val request = Request.Builder().url(pdfUrl).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) return@withPermit null
                        val body = response.body ?: return@withPermit null
                        FileOutputStream(tempFile).use { out ->
                            body.byteStream().copyTo(out)
                        }
                    }

                    val doc = LocalPdfDocument(context, tempFile.absolutePath)
                    val pageCount = doc.open()
                    val bmp = if (pageCount > 0) doc.renderPage(0, 300) else null
                    doc.close()

                    if (bmp != null) {
                        FileOutputStream(cached).use { out ->
                            bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        }
                    }
                    bmp
                } catch (e: Exception) {
                    null
                } finally {
                    try { tempFile.delete() } catch (_: Exception) {}
                }
            }
        }
}

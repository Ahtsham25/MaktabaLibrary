package com.shamiacademy.maktabalibrary.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.shamiacademy.maktabalibrary.data.AppDatabase
import com.shamiacademy.maktabalibrary.data.Book
import com.shamiacademy.maktabalibrary.data.DownloadedBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object BookDownloader {

    private const val SUBFOLDER = "MaktabaLibrary"

    fun localFileFor(context: Context, maktabaId: String, fileSlug: String): File {
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "$SUBFOLDER/$maktabaId"
        )
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$fileSlug.pdf")
    }

    fun startDownload(
        context: Context,
        maktabaId: String,
        maktabaNameUr: String,
        book: Book
    ) {
        val destFile = localFileFor(context, maktabaId, book.fileSlug)
        val request = DownloadManager.Request(Uri.parse(book.pdfDownloadUrl))
            .setTitle(book.title)
            .setDescription(maktabaNameUr)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destFile))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(request)

        // Register a one-shot receiver to save the DB row once download completes.
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.get(context).downloadedBookDao().insert(
                            DownloadedBook(
                                uid = book.uid,
                                title = book.title,
                                maktabaNameUr = maktabaNameUr,
                                localFilePath = destFile.absolutePath
                            )
                        )
                    }
                    try { context.unregisterReceiver(this) } catch (_: Exception) {}
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
    }
}

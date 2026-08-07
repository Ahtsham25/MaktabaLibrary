package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.data.AppDatabase
import com.shamiacademy.maktabalibrary.data.Book
import com.shamiacademy.maktabalibrary.data.FlatBook
import com.shamiacademy.maktabalibrary.util.BookDownloader
import com.shamiacademy.maktabalibrary.util.PdfThumbnailUtil
import kotlinx.coroutines.launch

class BookOptionsActivity : AppCompatActivity() {

    companion object {
        private const val EX_TITLE = "title"
        private const val EX_FILE_SLUG = "file_slug"
        private const val EX_ONLINE_URL = "online_url"
        private const val EX_PDF_URL = "pdf_url"
        private const val EX_MAKTABA_ID = "maktaba_id"
        private const val EX_MAKTABA_NAME = "maktaba_name"
        private const val EX_MAKTABA_COVER = "maktaba_cover"

        fun start(context: Context, flat: FlatBook) {
            context.startActivity(
                Intent(context, BookOptionsActivity::class.java)
                    .putExtra(EX_TITLE, flat.book.title)
                    .putExtra(EX_FILE_SLUG, flat.book.fileSlug)
                    .putExtra(EX_ONLINE_URL, flat.book.onlineViewUrl)
                    .putExtra(EX_PDF_URL, flat.book.pdfDownloadUrl)
                    .putExtra(EX_MAKTABA_ID, flat.maktabaId)
                    .putExtra(EX_MAKTABA_NAME, flat.maktabaNameUr)
                    .putExtra(EX_MAKTABA_COVER, flat.maktabaCoverUrl)
            )
        }
    }

    private lateinit var book: Book
    private lateinit var maktabaId: String
    private lateinit var maktabaNameUr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_options)

        val title = intent.getStringExtra(EX_TITLE) ?: ""
        val fileSlug = intent.getStringExtra(EX_FILE_SLUG) ?: ""
        val onlineUrl = intent.getStringExtra(EX_ONLINE_URL) ?: ""
        val pdfUrl = intent.getStringExtra(EX_PDF_URL) ?: ""
        maktabaId = intent.getStringExtra(EX_MAKTABA_ID) ?: ""
        maktabaNameUr = intent.getStringExtra(EX_MAKTABA_NAME) ?: ""
        val maktabaCover = intent.getStringExtra(EX_MAKTABA_COVER) ?: ""
        book = Book(title, fileSlug, onlineUrl, pdfUrl)

        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<android.widget.TextView>(R.id.text_title).text = title
        findViewById<android.widget.TextView>(R.id.text_maktaba).text = maktabaNameUr

        val imageCover = findViewById<android.widget.ImageView>(R.id.image_cover)
        Glide.with(this).load(maktabaCover).centerCrop().into(imageCover)

        findViewById<android.widget.Button>(R.id.btn_read_online).setOnClickListener {
            OnlineReaderActivity.start(this, onlineUrl, title)
        }

        findViewById<android.widget.Button>(R.id.btn_download).setOnClickListener {
            handleDownloadClick()
        }

        refreshDownloadState()
    }

    private fun handleDownloadClick() {
        lifecycleScope.launch {
            val existing = AppDatabase.get(this@BookOptionsActivity).downloadedBookDao().findByUid(book.uid)
            if (existing != null) {
                PdfReaderActivity.start(this@BookOptionsActivity, existing.localFilePath, existing.title)
            } else {
                findViewById<android.widget.TextView>(R.id.text_status).apply {
                    visibility = android.view.View.VISIBLE
                    text = getString(R.string.downloading)
                }
                BookDownloader.startDownload(this@BookOptionsActivity, maktabaId, maktabaNameUr, book)
                // Poll briefly for completion to update UI (system DownloadManager runs in background)
                var attempts = 0
                while (attempts < 60) {
                    kotlinx.coroutines.delay(2000)
                    val done = AppDatabase.get(this@BookOptionsActivity).downloadedBookDao().findByUid(book.uid)
                    if (done != null) { refreshDownloadState(); break }
                    attempts++
                }
            }
        }
    }

    private fun refreshDownloadState() {
        lifecycleScope.launch {
            val existing = AppDatabase.get(this@BookOptionsActivity).downloadedBookDao().findByUid(book.uid)
            val statusView = findViewById<android.widget.TextView>(R.id.text_status)
            val downloadBtn = findViewById<android.widget.Button>(R.id.btn_download)
            if (existing != null) {
                statusView.visibility = android.view.View.VISIBLE
                statusView.text = getString(R.string.downloaded)
                downloadBtn.text = getString(R.string.read_online).let { "کھولیں (ڈاؤن لوڈ شدہ)" }

                val bmp = PdfThumbnailUtil.getThumbnail(this@BookOptionsActivity, existing.localFilePath)
                if (bmp != null) {
                    findViewById<android.widget.ImageView>(R.id.image_cover).setImageBitmap(bmp)
                }
            } else {
                statusView.visibility = android.view.View.GONE
                downloadBtn.text = getString(R.string.download)
            }
        }
    }
}

package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.adapter.BookGridAdapter
import com.shamiacademy.maktabalibrary.data.AppDatabase
import com.shamiacademy.maktabalibrary.data.FlatBook
import com.shamiacademy.maktabalibrary.data.LibraryRepository
import kotlinx.coroutines.launch

class AuthorBooksActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_MAKTABA_ID = "maktaba_id"

        fun start(context: Context, maktabaId: String) {
            val intent = Intent(context, AuthorBooksActivity::class.java).apply {
                putExtra(EXTRA_MAKTABA_ID, maktabaId)
                setPackage(context.packageName)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var adapter: BookGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_books)

        val maktabaId = intent.getStringExtra(EXTRA_MAKTABA_ID) ?: return finish()
        val recycler = findViewById<RecyclerView>(R.id.recycler_books)
        val progress = findViewById<android.widget.ProgressBar>(R.id.progress)
        val titleView = findViewById<android.widget.TextView>(R.id.text_title)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }

        adapter = BookGridAdapter(emptyList(), emptyMap(), lifecycleScope) { flat ->
            BookOptionsActivity.start(this, flat)
        }
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter

        progress.visibility = android.view.View.VISIBLE
        lifecycleScope.launch {
            val maktabas = LibraryRepository.getMaktabas(this@AuthorBooksActivity)
            val maktaba = maktabas.find { it.id == maktabaId }
            titleView.text = maktaba?.nameUr ?: ""

            val flatBooks: List<FlatBook> = maktaba?.books?.map { book ->
                // آٹو امیج لاجک: اگر JSON میں الگ تصویر نہیں ہے تو URL کے ذریعے پیج 1 کی تصویر بن جائے گی
                val bookCover = when {
                    !book.coverImageUrl.isNullOrEmpty() -> book.coverImageUrl
                    book.url.contains("archive.org") && book.url.contains(".pdf") -> {
                        book.url.replace("/details/", "/download/") + "_page_0001.jpg"
                    }
                    else -> maktaba.coverImageUrl
                }
                FlatBook(maktaba.nameUr, maktaba.id, bookCover, book)
            } ?: emptyList()

            adapter.updateData(flatBooks)
            progress.visibility = android.view.View.GONE

            val downloaded = AppDatabase.get(this@AuthorBooksActivity).downloadedBookDao().getAllOnce()
            adapter.updateDownloadedMap(downloaded.associate { it.uid to it.localFilePath })
        }
    }
}

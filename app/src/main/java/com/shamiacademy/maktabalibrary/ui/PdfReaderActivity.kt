package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.adapter.PdfPageAdapter
import com.shamiacademy.maktabalibrary.util.LocalPdfDocument
import kotlinx.coroutines.launch

class PdfReaderActivity : AppCompatActivity() {

    companion object {
        private const val EX_PATH = "path"
        private const val EX_TITLE = "title"
        fun start(context: Context, localPath: String, title: String) {
            context.startActivity(
                Intent(context, PdfReaderActivity::class.java)
                    .putExtra(EX_PATH, localPath)
                    .putExtra(EX_TITLE, title)
            )
        }
    }

    private var document: LocalPdfDocument? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_reader)

        val path = intent.getStringExtra(EX_PATH) ?: return finish()
        val title = intent.getStringExtra(EX_TITLE) ?: ""

        findViewById<android.widget.TextView>(R.id.text_title).text = title
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val pager = findViewById<ViewPager2>(R.id.pager)
        val progress = findViewById<android.widget.ProgressBar>(R.id.progress_loading)
        val pageCountText = findViewById<android.widget.TextView>(R.id.text_page_count)

        progress.visibility = View.VISIBLE
        val widthPx = resources.displayMetrics.widthPixels

        lifecycleScope.launch {
            val doc = LocalPdfDocument(this@PdfReaderActivity, path)
            document = doc
            val count = try { doc.open() } catch (e: Exception) { 0 }
            progress.visibility = View.GONE

            if (count == 0) {
                findViewById<android.widget.TextView>(R.id.text_page_count).text = "کتاب کھولنے میں مسئلہ پیش آیا"
                return@launch
            }

            pager.adapter = PdfPageAdapter(doc, count, lifecycleScope, widthPx)
            pageCountText.text = "1 / $count"
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    pageCountText.text = "${position + 1} / $count"
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch { document?.close() }
    }
}

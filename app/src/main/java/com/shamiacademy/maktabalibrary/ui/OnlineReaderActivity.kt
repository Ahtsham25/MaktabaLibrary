package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.maktabalibrary.R

class OnlineReaderActivity : AppCompatActivity() {

    companion object {
        private const val EX_URL = "url"
        private const val EX_TITLE = "title"
        fun start(context: Context, url: String, title: String) {
            context.startActivity(
                Intent(context, OnlineReaderActivity::class.java)
                    .putExtra(EX_URL, url)
                    .putExtra(EX_TITLE, title)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_reader)

        val url = intent.getStringExtra(EX_URL) ?: ""
        val title = intent.getStringExtra(EX_TITLE) ?: ""

        findViewById<android.widget.TextView>(R.id.text_title).text = title
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val progress = findViewById<android.widget.ProgressBar>(R.id.progress)
        val webView = findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress.visibility = View.GONE
            }
        }
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        val webView = findViewById<WebView>(R.id.webview)
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}

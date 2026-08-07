package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.maktabalibrary.R

class OnlineReaderActivity : AppCompatActivity() {

    companion object {
        private const val EX_URL = "url"
        private const val EX_TITLE = "title"

        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, OnlineReaderActivity::class.java).apply {
                putExtra(EX_URL, url)
                putExtra(EX_TITLE, title)
                setPackage(context.packageName)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        makeFullscreen()
        setContentView(R.layout.activity_online_reader)

        val rawUrl = intent.getStringExtra(EX_URL) ?: ""

        // آرکائیو کے ڈسکرپشن پیج کے بجائے صرف فل اسکرین ایٹمبیڈ ریڈر میں تبدیل کرنے کا کوڈ
        val formattedUrl = if (rawUrl.contains("archive.org/details/")) {
            rawUrl.replace("archive.org/details/", "archive.org/embed/")
        } else {
            rawUrl
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val progress = findViewById<android.widget.ProgressBar>(R.id.progress)
        val webView = findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress.visibility = View.GONE
            }
        }
        
        webView.loadUrl(formattedUrl)
    }

    private fun makeFullscreen() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) makeFullscreen()
    }

    override fun onBackPressed() {
        val webView = findViewById<WebView>(R.id.webview)
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}

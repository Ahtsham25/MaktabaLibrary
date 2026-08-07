package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.maktabalibrary.R

class WebPageActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_BODY = "body"
        fun start(context: Context, title: String, body: String) {
            context.startActivity(
                Intent(context, WebPageActivity::class.java)
                    .putExtra(EXTRA_TITLE, title)
                    .putExtra(EXTRA_BODY, body)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)

        findViewById<android.widget.TextView>(R.id.text_title).text = intent.getStringExtra(EXTRA_TITLE)
        findViewById<android.widget.TextView>(R.id.text_body).text = intent.getStringExtra(EXTRA_BODY)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
    }
}

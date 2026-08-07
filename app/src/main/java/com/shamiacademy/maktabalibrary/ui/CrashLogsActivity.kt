package com.shamiacademy.maktabalibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.util.CrashHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashLogsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CrashLogsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_logs)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val recycler = findViewById<RecyclerView>(R.id.recycler_logs)
        val emptyText = findViewById<android.widget.TextView>(R.id.text_empty)

        val logs = CrashHandler.listLogs(this)
        emptyText.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_crash_log, parent, false)
                return VH(v)
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val file = logs[position]
                val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(file.lastModified()))
                holder.name.text = dateFmt

                holder.itemView.setOnClickListener {
                    WebPageActivity.start(this@CrashLogsActivity, "کریش لاگ", file.readText())
                }
                holder.share.setOnClickListener {
                    shareFile(file)
                }
            }

            override fun getItemCount() = logs.size
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "کریش لاگ شیئر کریں"))
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: android.widget.TextView = view.findViewById(R.id.text_name)
        val share: android.widget.TextView = view.findViewById(R.id.text_share)
    }
}

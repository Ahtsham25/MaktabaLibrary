package com.shamiacademy.maktabalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.data.DownloadedBook
import com.shamiacademy.maktabalibrary.util.PdfThumbnailUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DownloadedAdapter(
    private var items: List<DownloadedBook>,
    private val scope: CoroutineScope,
    private val onClick: (DownloadedBook) -> Unit
) : RecyclerView.Adapter<DownloadedAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: android.widget.ImageView = view.findViewById(R.id.image_cover)
        val title: android.widget.TextView = view.findViewById(R.id.text_title)
        val maktaba: android.widget.TextView = view.findViewById(R.id.text_maktaba)
        var job: Job? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded_book, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.maktaba.text = item.maktabaNameUr
        holder.job?.cancel()
        holder.job = scope.launch {
            val bmp = PdfThumbnailUtil.getThumbnail(holder.image.context, item.localFilePath)
            if (bmp != null) holder.image.setImageBitmap(bmp)
        }
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<DownloadedBook>) {
        items = newItems
        notifyDataSetChanged()
    }
}

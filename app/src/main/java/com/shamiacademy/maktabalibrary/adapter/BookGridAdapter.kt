package com.shamiacademy.maktabalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.data.FlatBook
import com.shamiacademy.maktabalibrary.util.PdfThumbnailUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * uidToLocalPath: map of downloaded book.uid -> local pdf file path.
 * When a book is present here, its own first-page render is shown as the
 * thumbnail. Otherwise the maktaba's shared cover image is used.
 */
class BookGridAdapter(
    private var items: List<FlatBook>,
    private var uidToLocalPath: Map<String, String>,
    private val scope: CoroutineScope,
    private val onClick: (FlatBook) -> Unit
) : RecyclerView.Adapter<BookGridAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: android.widget.ImageView = view.findViewById(R.id.image_cover)
        val title: android.widget.TextView = view.findViewById(R.id.text_title)
        val tick: android.widget.ImageView = view.findViewById(R.id.icon_downloaded)
        var job: Job? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_book_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.book.title
        holder.job?.cancel()

        val localPath = uidToLocalPath[item.book.uid]
        if (localPath != null) {
            holder.tick.visibility = View.VISIBLE
            holder.job = scope.launch {
                val bmp = PdfThumbnailUtil.getThumbnail(holder.image.context, localPath)
                if (bmp != null) {
                    holder.image.setImageBitmap(bmp)
                } else {
                    Glide.with(holder.image.context).load(item.maktabaCoverUrl).centerCrop().into(holder.image)
                }
            }
        } else {
            holder.tick.visibility = View.GONE
            Glide.with(holder.image.context)
                .load(item.maktabaCoverUrl)
                .placeholder(R.color.surface_black)
                .centerCrop()
                .into(holder.image)
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<FlatBook>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateDownloadedMap(map: Map<String, String>) {
        uidToLocalPath = map
        notifyDataSetChanged()
    }
}

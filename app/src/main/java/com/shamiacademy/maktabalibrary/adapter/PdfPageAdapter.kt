package com.shamiacademy.maktabalibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.util.LocalPdfDocument
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PdfPageAdapter(
    private val document: LocalPdfDocument,
    private val pageCount: Int,
    private val scope: LifecycleCoroutineScope,
    private val pageWidthPx: Int
) : RecyclerView.Adapter<PdfPageAdapter.VH>() {

    class VH(val image: ImageView) : RecyclerView.ViewHolder(image) {
        var job: Job? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_page, parent, false)
        return VH(view.findViewById(R.id.image_page))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.job?.cancel()
        holder.image.setImageBitmap(null)
        holder.job = scope.launch {
            val bmp = document.renderPage(position, pageWidthPx)
            if (bmp != null) holder.image.setImageBitmap(bmp)
        }
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.job?.cancel()
    }

    override fun getItemCount() = pageCount
}

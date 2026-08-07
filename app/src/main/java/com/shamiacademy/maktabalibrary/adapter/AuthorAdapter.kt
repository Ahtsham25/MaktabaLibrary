package com.shamiacademy.maktabalibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.data.Maktaba

class AuthorAdapter(
    private var items: List<Maktaba>,
    private val onClick: (Maktaba) -> Unit
) : RecyclerView.Adapter<AuthorAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_cover)
        val title: TextView = view.findViewById(R.id.text_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_author_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.nameUr

        Glide.with(holder.image.context)
            .load(item.coverImageUrl)
            .placeholder(R.color.surface_black)
            .centerCrop()
            .into(holder.image)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Maktaba>) {
        items = newItems
        notifyDataSetChanged()
    }
}

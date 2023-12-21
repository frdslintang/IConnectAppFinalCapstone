package com.dicoding.myiconnect.ui.articelfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.myiconnect.R


class ListArticelAdapter(
    private val context: Context,
    private val items: List<Items>,
    private val itemClickListener: (Items) -> Unit
) : RecyclerView.Adapter<ListArticelAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_articel, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentItem = items[position]

        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.description

        // Load gambar dari URL menggunakan Glide
        Glide.with(context)
            .load(currentItem.photoUrl) // Memuat gambar dari URL
            .placeholder(R.drawable.baseline_clear_24) // Placeholder jika gambar tidak tersedia atau sedang dimuat
            .error(R.drawable.baseline_clear_24) // Gambar yang ditampilkan jika ada kesalahan dalam memuat gambar
            .into(holder.photoImageView)

        // Tambahkan listener klik di sini
        holder.itemView.setOnClickListener {
            val bundle = bundleOf(
                "title" to currentItem.title,
                "description" to currentItem.description,
                "photo" to currentItem.photoUrl // Kirim URL gambar
            )
            // Lakukan sesuatu dengan bundle jika item diklik
            itemClickListener(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_item_tittle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)
        val photoImageView: ImageView = itemView.findViewById(R.id.img_item_photo)
    }
}


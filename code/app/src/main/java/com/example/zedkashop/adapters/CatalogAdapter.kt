package com.example.zedkashop.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zedkashop.R

class CatalogAdapter(
    private val items: List<CatalogItem>,
    private val onCatalogClick: (CatalogItem) -> Unit
) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imageView.context)
            .load(item.imageResId)
            .override(100, 100)
            .centerCrop()
            .into(holder.imageView)

        holder.textView.text = item.name

        holder.itemView.setOnClickListener {
            onCatalogClick(item) // Обработка клика
        }
    }

    override fun getItemCount() = items.size
}
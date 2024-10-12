package com.example.zedkashop.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB

class ProductAdapter(
    private val products: List<ProductDB>,
    private val onProductClick: (ProductDB) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val addToCart: ImageView = itemView.findViewById(R.id.addToCart)
        fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price

            Glide.with(itemView.context)
                .load(product.imageUrl) // URL изображения из объекта ProductDB
                .apply(RequestOptions()
                    //   .placeholder(R.drawable.placeholder) // Замените на ваш ресурс для загрузки
                  //  .error(R.drawable.error) // Замените на ваш ресурс для ошибки
                )
                .into(productImage)
        }
    }
}
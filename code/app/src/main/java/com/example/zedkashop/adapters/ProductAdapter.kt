package com.example.zedkashop.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB

class ProductAdapter(
    private val products: List<ProductDB>,
    private val onProductClick: (ProductDB) -> Unit // Обработчик клика
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
            onProductClick(product) // Обработка клика
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val addToCartButton: ImageView = itemView.findViewById(R.id.addToCartButton)

        fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)

            addToCartButton.setOnClickListener {
                // Handle the add to cart action here
                // For example, you can add the product to a cart list or show a toast
                Toast.makeText(
                    itemView.context,
                    "${product.name} added to cart",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
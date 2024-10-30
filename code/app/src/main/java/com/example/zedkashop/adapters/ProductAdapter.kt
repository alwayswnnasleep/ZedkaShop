package com.example.zedkashop.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB

class ProductAdapter(
    private val context: Context,
    private val products: List<ProductDB>,
    private val onProductClick: (ProductDB) -> Unit,
    private val onAddToCartClick: (ProductDB) -> Unit,
    private val onQuantityChange: ((ProductDB, Int) -> Unit)? = null, // Для корзины
    private val isInCartFragment: Boolean = false // Новый параметр
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PRODUCT = 0
        const val VIEW_TYPE_CART = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isInCartFragment) {
            VIEW_TYPE_CART
        } else {
            VIEW_TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_CART) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_cart, parent, false) // Разметка для корзины
            CartViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false) // Стандартная разметка
            ProductViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        if (holder is CartViewHolder && onQuantityChange != null) {
            holder.buttonDecrease.setOnClickListener {
                val currentQuantity = holder.quantityCounter.text.toString().toInt()
                if (currentQuantity > 1) {
                    holder.quantityCounter.text = (currentQuantity - 1).toString()
                    onQuantityChange.invoke(product, currentQuantity - 1) // Используем безопасный вызов
                }
            }

            holder.buttonIncrease.setOnClickListener {
                val currentQuantity = holder.quantityCounter.text.toString().toInt()
                holder.quantityCounter.text = (currentQuantity + 1).toString()
                onQuantityChange.invoke(product, currentQuantity + 1) // Используем безопасный вызов
            }
        }

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        if (holder is ProductViewHolder) {
            holder.addToCartButton.setOnClickListener {
                onAddToCartClick(product) // Добавление товара в корзину
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(product: ProductDB)
    }

    inner class ProductViewHolder(itemView: View) : ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val addToCartButton: ImageView = itemView.findViewById(R.id.addToCartButton)

        override fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)
        }
    }

    inner class CartViewHolder(itemView: View) : ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val buttonDecrease: Button = itemView.findViewById(R.id.buttonDecrease)
        val buttonIncrease: Button = itemView.findViewById(R.id.buttonIncrease)
        val quantityCounter: TextView = itemView.findViewById(R.id.quantityCounter)

        override fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price
            quantityCounter.text = "1" // Изначально устанавливаем 1

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)
        }
    }
}
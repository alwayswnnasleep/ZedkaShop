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
import com.example.zedkashop.ui.manageproducts.ManageProductsFragment

class ProductAdapter(

    private val context: Context,
    private val products: List<ProductDB>,
    private val onProductClick: (ProductDB) -> Unit,
    private val onAddToCartClick: (ProductDB) -> Unit,

    private val onQuantityChange: ((ProductDB, Int) -> Unit)? = null,
    private val productQuantities: Map<String, Int> = emptyMap(), // Pass the map of quantities
    private val isInCartFragment: Boolean = false,
    private val isInManageFragment: Boolean = false // New parameter for ManageFragment
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PRODUCT = 0
        const val VIEW_TYPE_CART = 1
        const val VIEW_TYPE_MANAGE = 2 // New view type for ManageFragment
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isInManageFragment -> VIEW_TYPE_MANAGE
            isInCartFragment -> VIEW_TYPE_CART
            else -> VIEW_TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CART -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product_cart, parent, false)
                CartViewHolder(view)
            }

            VIEW_TYPE_MANAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product_manage, parent, false)
                ManageViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                ProductViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        if (holder is CartViewHolder && onQuantityChange != null) {
            val initialQuantity =
                productQuantities[product.id] ?: 1 // Get the initial quantity from map
            holder.quantityCounter.text =
                initialQuantity.toString() // Set the correct initial quantity

            holder.buttonDecrease.setOnClickListener {
                val currentQuantity = holder.quantityCounter.text.toString().toInt()
                if (currentQuantity > 1) {
                    val newQuantity = currentQuantity - 1
                    holder.quantityCounter.text = newQuantity.toString()
                    onQuantityChange.invoke(product, newQuantity)
                    updateButtonStates(holder, newQuantity)
                }
            }

            holder.buttonIncrease.setOnClickListener {
                val currentQuantity = holder.quantityCounter.text.toString().toInt()
                val newQuantity = currentQuantity + 1
                holder.quantityCounter.text = newQuantity.toString()
                onQuantityChange.invoke(product, newQuantity)
                updateButtonStates(holder, newQuantity)
            }

            // Initialize button states
            updateButtonStates(holder, initialQuantity)
        }

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        if (holder is ProductViewHolder) {
            holder.addToCartButton.setOnClickListener {
                onAddToCartClick(product)
            }
        }


        if (holder is ManageViewHolder) {

        }
    }

    private fun updateButtonStates(holder: CartViewHolder, quantity: Int) {
        holder.buttonDecrease.isEnabled = quantity > 1
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
        private val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        private val productConsumer: TextView = itemView.findViewById(R.id.productManufacturer)
        override fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price
            productCategory.text = product.category
            productConsumer.text = product.consumer
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)
        }
    }

    // New ViewHolder for ManageFragment
    inner class ManageViewHolder(itemView: View) : ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val manageButton: Button = itemView.findViewById(R.id.manageButton)
        private val productViews: TextView = itemView.findViewById(R.id.viewCount)
        private val productPurchases: TextView = itemView.findViewById(R.id.purchaseCount)
        private val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        private val productConsumer: TextView = itemView.findViewById(R.id.productManufacturer)

        override fun bind(product: ProductDB) {
            productName.text = product.name
            productPrice.text = product.price
            productCategory.text = product.category
            productConsumer.text = product.consumer
            productViews.text = product.views.toString()
            productPurchases.text = product.purchases.toString()

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)


        }

    }
}
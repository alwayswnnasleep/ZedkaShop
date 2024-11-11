package com.example.zedkashop.ui.manageproducts

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

class ManageProductAdapter(
    private val context: Context,
    private val products: List<ProductDB>,
    private val onProductClick: (ProductDB) -> Unit,
    private val onManageButtonClick: (ProductDB) -> Unit // New parameter for manage button
) : RecyclerView.Adapter<ManageProductAdapter.ManageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_manage, parent, false)
        return ManageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.manageButton.setOnClickListener {
            onManageButtonClick(product) // Handle manage button click
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ManageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val manageButton: Button = itemView.findViewById(R.id.manageButton)
        private val productViews: TextView = itemView.findViewById(R.id.viewCount)
        private val productPurchases: TextView = itemView.findViewById(R.id.purchaseCount)
        private val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        private val productConsumer: TextView = itemView.findViewById(R.id.productManufacturer)

        fun bind(product: ProductDB) {
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
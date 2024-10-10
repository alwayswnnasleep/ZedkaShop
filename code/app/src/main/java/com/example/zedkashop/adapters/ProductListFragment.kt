package com.example.zedkashop.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ProductListFragment : Fragment(R.layout.fragment_product_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<ProductDB>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        ) // Горизонтальная прокрутка
        productAdapter = ProductAdapter(productList) { product ->
            val bundle = Bundle().apply {
                putSerializable("product", product) // Передаем продукт как Serializable
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.productDetailFragment, ProductDetailFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
        loadProducts()
    }

    private fun loadProducts() {
        productList.clear() // Очистите список перед загрузкой
        firestore.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val product = document.toObject(ProductDB::class.java)
                    productList.add(product)
                }

                productAdapter.notifyDataSetChanged() // Уведомите адаптер об изменениях
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
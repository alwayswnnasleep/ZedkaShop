package com.example.zedkashop.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        productAdapter = ProductAdapter(productList) { product ->
            val bundle = Bundle().apply {
                putSerializable("product", product) // Передаем продукт как Serializable
            }
            // Используйте навигацию
            findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
        }

        recyclerView.adapter = productAdapter // Установите адаптер для recyclerView
        loadProducts()
    }

    private fun loadProducts() {
        // Очистите список перед загрузкой
        productList.clear()
        productAdapter.notifyDataSetChanged() // Уведомите адаптер об очистке списка

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
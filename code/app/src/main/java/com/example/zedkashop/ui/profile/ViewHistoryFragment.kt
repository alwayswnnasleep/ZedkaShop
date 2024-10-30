package com.example.zedkashop.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ViewHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val products = mutableListOf<ProductDB>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_view_history, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Передаем контекст, список продуктов и обработчик клика
        productAdapter = ProductAdapter(requireContext(), products, { product -> onProductClick(product) }, { /* Обработчик добавления в корзину можно оставить пустым, если не нужен */ })
        recyclerView.adapter = productAdapter

        loadProducts() // Загружаем продукты сразу

        return view
    }

    private fun loadProducts() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val product = document.toObject(ProductDB::class.java)
                    products.add(product)
                }

                // Сортируем продукты по количеству просмотров в порядке убывания
                products.sortByDescending { it.views }

                productAdapter.notifyDataSetChanged()
                Log.d("ViewHistoryFragment", "Loaded ${products.size} products")
            }
            .addOnFailureListener { exception ->
                Log.e("ViewHistoryFragment", "Error loading products", exception)
            }
    }

    private fun onProductClick(product: ProductDB) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }
        findNavController().navigate(R.id.action_navigation_home_to_productDetailFragment, bundle)
    }
}
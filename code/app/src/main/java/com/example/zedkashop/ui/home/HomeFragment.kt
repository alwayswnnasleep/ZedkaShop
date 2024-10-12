package com.example.zedkashop.ui.home

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val products = mutableListOf<ProductDB>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // Горизонтальная прокрутка
        productAdapter = ProductAdapter(products) { product -> onProductClick(product) }
        recyclerView.adapter = productAdapter

        loadProducts() // Загружаем продукты сразу

        return view
    }

    private fun loadProducts() {
        val db: FirebaseFirestore = Firebase.firestore

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
                Log.d("HomeFragment", "Loaded ${products.size} products")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading products", exception)
            }
    }

    private fun onProductClick(product: ProductDB) {
        // Увеличиваем количество просмотров
        product.views += 1

        // Создаем Bundle для передачи данных
        val bundle = Bundle().apply {
            putSerializable("product", product) // Передаем продукт как Serializable
        }

        // Переход на страницу деталей продукта
        findNavController().navigate(R.id.action_navigation_home_to_productDetailFragment, bundle)
    }
}
package com.example.zedkashop.ui.home

import android.content.Context
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

        product.views += 1


        addToViewHistory(product)


        val bundle = Bundle().apply {
            putSerializable("product", product)
        }


        findNavController().navigate(R.id.action_navigation_home_to_productDetailFragment, bundle)
    }

    // Метод для добавления продукта в историю просмотров
    private fun addToViewHistory(product: ProductDB) {
        // Получаем SharedPreferences для сохранения истории
        val sharedPreferences = requireContext().getSharedPreferences("view_history", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Получаем текущую историю просмотров
        val currentHistory = sharedPreferences.getStringSet("history", mutableSetOf()) ?: mutableSetOf()

        // Добавляем новый продукт (ID или другой уникальный идентификатор)
        currentHistory.add(product.id.toString()) // Предполагается, что у продукта есть уникальный ID

        // Сохраняем обновленную историю
        editor.putStringSet("history", currentHistory)
        editor.apply()
    }
}
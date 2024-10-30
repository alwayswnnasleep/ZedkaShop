package com.example.zedkashop.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        productAdapter = ProductAdapter(requireContext(), products, { product -> onProductClick(product) }, { product -> addToCart(product) })
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
        val sharedPreferences = requireContext().getSharedPreferences("view_history", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val currentHistory = sharedPreferences.getStringSet("history", mutableSetOf()) ?: mutableSetOf()
        currentHistory.add(product.toString())

        editor.putStringSet("history", currentHistory)
        editor.apply()
    }

    private fun addToCart(product: ProductDB) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val cartRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("cart")
                .document(product.id) // Используем ID продукта как ID документа

            // Проверяем, существует ли уже продукт в корзине
            cartRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentQuantity = document.getLong("quantity") ?: 0
                        cartRef.update("quantity", currentQuantity + 1)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Количество товара обновлено", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("HomeFragment", "Ошибка при обновлении количества в корзине", e)
                            }
                    } else {
                        cartRef.set(mapOf("productId" to product.id, "quantity" to 1))
                            .addOnSuccessListener {
                                Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("HomeFragment", "Ошибка при добавлении в корзину", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeFragment", "Ошибка при проверке наличия в корзине", e)
                }
        } else {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
        }
    }
}
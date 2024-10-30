package com.example.zedkashop.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var totalPriceTextView: TextView
    private lateinit var buyButton: Button
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<ProductDB>()
    private var totalPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        buyButton = view.findViewById(R.id.buyButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Передаем необходимые параметры в ProductAdapter
        productAdapter = ProductAdapter(requireContext(), productList, { product ->
            // Обработка клика по продукту
            val bundle = Bundle().apply {
                putSerializable("product", product)
            }
            findNavController().navigate(R.id.action_navigation_cart_to_productDetailFragment, bundle)
        }, { /* Обработчик добавления в корзину, если нужен */ })

        recyclerView.adapter = productAdapter // Установите адаптер для recyclerView
        loadCartProducts()

        buyButton.setOnClickListener {
            // Логика для покупки товаров
            purchaseProducts()
        }
    }

    private fun loadCartProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Ссылка на подколлекцию "cart" для текущего пользователя
            val cartRef = firestore.collection("users").document(userId).collection("cart")

            cartRef.get()
                .addOnSuccessListener { snapshot ->
                    Log.d("CartFragment", "Snapshot size: ${snapshot.size()}") // Логируем количество документов
                    val productIds = snapshot.documents.mapNotNull { it.getString("productId") }
                    Log.d("CartFragment", "Cart items: $productIds")
                    productList.clear()
                    totalPrice = 0.0

                    if (productIds.isEmpty()) {
                        totalPriceTextView.text = "Корзина пуста"
                    } else {
                        for (productId in productIds) {
                            firestore.collection("products").document(productId)
                                .get()
                                .addOnSuccessListener { productDoc ->
                                    val product = productDoc.toObject(ProductDB::class.java)
                                    if (product != null) {
                                        productList.add(product)

                                        // Преобразуем строку в число
                                        val productPrice = product.price.toDoubleOrNull() ?: 0.0
                                        totalPrice += productPrice

                                        Log.d("CartFragment", "Добавлен продукт: ${product.name}, цена: ${product.price}")
                                    } else {
                                        Log.e("CartFragment", "Продукт не найден для ID: $productId")
                                    }

                                    // Обновляем адаптер и текст общей стоимости
                                    productAdapter.notifyDataSetChanged()
                                    totalPriceTextView.text = "Общая стоимость: $totalPrice"
                                    firestore.collection("products").document(productId)
                                        .get()
                                        .addOnSuccessListener { productDoc ->
                                            if (productDoc.exists()) {
                                                val product = productDoc.toObject(ProductDB::class.java)
                                                if (product != null) {
                                                    productList.add(product)
                                                    // Обновление UI
                                                }
                                            } else {
                                                Log.e("CartFragment", "Продукт с ID $productId не существует в коллекции products")
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("CartFragment", "Ошибка при загрузке продукта: $e")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CartFragment", "Ошибка при загрузке продукта: $e")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CartFragment", "Ошибка при загрузке корзины: $e")
                }
        } else {
            Log.e("CartFragment", "Пользователь не авторизован")
        }
    }

    private fun purchaseProducts() {
        // Логика для обработки покупки
        Log.d("CartFragment", "Покупка товаров: $totalPrice")
        // Здесь можно добавить логику для создания заказа, очистки корзины и т. д.
    }
}
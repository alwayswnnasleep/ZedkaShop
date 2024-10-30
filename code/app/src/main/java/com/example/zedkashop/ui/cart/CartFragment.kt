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
    private val productQuantities = mutableMapOf<String, Int>() // Для хранения количеств продуктов
    private var totalPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        buyButton = view.findViewById(R.id.buyButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productAdapter = ProductAdapter(
            requireContext(),
            productList,
            { product -> navigateToProductDetail(product) },
            { /* Обработчик добавления в корзину не нужен в корзине */ },
            ::onQuantityChange,
            isInCartFragment = true
        )

        recyclerView.adapter = productAdapter
        loadCartProducts()

        buyButton.setOnClickListener {
            purchaseProducts()
        }
    }

    private fun navigateToProductDetail(product: ProductDB) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }
        findNavController().navigate(R.id.action_navigation_cart_to_productDetailFragment, bundle)
    }

    private fun loadCartProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = firestore.collection("users").document(userId).collection("cart")

        cartRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                totalPriceTextView.text = "Корзина пуста"
                return@addOnSuccessListener
            }

            totalPrice = 0.0
            productList.clear()
            productQuantities.clear() // Очистка количеств

            val productIds = snapshot.documents.mapNotNull { it.getString("productId") }
            loadProducts(productIds)
        }.addOnFailureListener { e ->
            Log.e("CartFragment", "Ошибка при загрузке корзины: $e")
        }
    }

    private fun loadProducts(productIds: List<String>) {
        productIds.forEach { productId ->
            firestore.collection("products").document(productId).get()
                .addOnSuccessListener { productDoc ->
                    val product = productDoc.toObject(ProductDB::class.java)
                    product?.let {
                        productList.add(it)
                        // Получение количества из корзины
                        val quantity = productQuantities[product.id] ?: 1 // По умолчанию 1
                        productQuantities[product.id] = quantity // Сохранение количества

                        // Обновляем общую стоимость
                        totalPrice += (it.price.toDoubleOrNull() ?: 0.0) * quantity
                        totalPriceTextView.text = "Общая стоимость: $totalPrice"
                        productAdapter.notifyDataSetChanged()
                    } ?: Log.e("CartFragment", "Продукт не найден для ID: $productId")
                }.addOnFailureListener { e ->
                    Log.e("CartFragment", "Ошибка при загрузке продукта: $e")
                }
        }
    }

    private fun onQuantityChange(product: ProductDB, newQuantity: Int) {
        // Обновляем количество и пересчитываем общую стоимость
        productQuantities[product.id] = newQuantity
        totalPrice = productList.sumOf { product ->
            (product.price.toDoubleOrNull() ?: 0.0) * (productQuantities[product.id] ?: 1)
        }
        totalPriceTextView.text = "Общая стоимость: $totalPrice"
    }

    private fun purchaseProducts() {
        Log.d("CartFragment", "Покупка товаров: $totalPrice")
        // Логика для обработки покупки
    }
}
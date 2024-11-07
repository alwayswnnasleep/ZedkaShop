package com.example.zedkashop.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewPurchaseHistoryFragment : Fragment(R.layout.fragment_purchase_history) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<ProductDB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "История покупок"

        // Инициализация RecyclerView с LinearLayoutManager для отображения в списке
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Список

        productAdapter = ProductAdapter(requireContext(), productList, { product ->
            navigateToProductDetail(product)
        }, { /* Обработчик для добавления в корзину не нужен в истории */ })
        recyclerView.adapter = productAdapter

        loadPurchasedProducts()
        setupBackButton()
    }

    private fun navigateToProductDetail(product: ProductDB) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }
        findNavController().navigate(R.id.action_viewPurchaseHistoryFragment2_to_productDetailFragment, bundle)
    }

    private fun loadPurchasedProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val historyRef = firestore.collection("users").document(userId).collection("history_purchase")

        historyRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d("ViewPurchaseHistoryFragment", "История покупок пуста")
                return@addOnSuccessListener
            }
            productList.clear()
            snapshot.documents.forEach { document ->
                val productId = document.getString("productId") ?: return@forEach
                loadProduct(productId)
            }
        }.addOnFailureListener { e ->
            Log.e("ViewPurchaseHistoryFragment", "Ошибка при загрузке истории покупок: $e")
        }
    }

    private fun loadProduct(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { productDoc ->
                val product = productDoc.toObject(ProductDB::class.java)
                product?.let {
                    productList.add(it)
                    productAdapter.notifyDataSetChanged() // Обновляем адаптер
                } ?: Log.e("ViewPurchaseHistoryFragment", "Продукт не найден для ID: $productId")
            }.addOnFailureListener { e ->
                Log.e("ViewPurchaseHistoryFragment", "Ошибка при загрузке продукта: $e")
            }
    }

    private fun setupBackButton() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
package com.example.zedkashop.ui.home

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.cart.CartManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchEditText: EditText
    private val products = mutableListOf<ProductDB>()
    private val filteredProducts = mutableListOf<ProductDB>()
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        (activity as AppCompatActivity).supportActionBar?.hide()
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        productAdapter = ProductAdapter(requireContext(), filteredProducts,
            { product -> onProductClick(product) },
            { product -> addToCart(product) }
        )
        recyclerView.adapter = productAdapter

        loadProducts() // Загружаем продукты сразу

        // Фильтрация продуктов по тексту в поле поиска
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun loadProducts() {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                products.clear()
                filteredProducts.clear()

                for (document in result) {
                    val product = document.toObject(ProductDB::class.java)
                    products.add(product)
                }

                filteredProducts.addAll(products)
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterProducts(query: String) {
        filteredProducts.clear()
        if (query.isEmpty()) {
            filteredProducts.addAll(products)
        } else {
            for (product in products) {
                if (product.name.contains(query, ignoreCase = true)) {
                    filteredProducts.add(product)
                }
            }
        }
        productAdapter.notifyDataSetChanged()
    }

    private fun onProductClick(product: ProductDB) {
        product.views += 1
        addToViewHistory(product)

        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        findNavController().navigate(R.id.action_navigation_home_to_productDetailFragment, bundle)
    }

    private fun addToViewHistory(product: ProductDB) {
        val sharedPreferences = requireContext().getSharedPreferences("view_history", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val currentHistory = sharedPreferences.getStringSet("history", mutableSetOf()) ?: mutableSetOf()
        currentHistory.add(product.toString())

        editor.putStringSet("history", currentHistory)
        editor.apply()
    }

    private fun addToCart(product: ProductDB) {
        CartManager.addToCart(requireContext(), product)
    }
}
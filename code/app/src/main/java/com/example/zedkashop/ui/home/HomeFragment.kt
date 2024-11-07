package com.example.zedkashop.ui.home

import BrandAdapter
import BrandDB
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.cart.CartManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zedkashop.ui.history.HistoryManager


class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var brandsRecyclerView: RecyclerView
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var searchEditText: EditText
    private lateinit var progressBar: ProgressBar
    private val products = mutableListOf<ProductDB>()
    private val filteredProducts = mutableListOf<ProductDB>()
    private val brands = mutableListOf<BrandDB>()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Инициализация RecyclerView для продуктов и брендов
        recyclerView = view.findViewById(R.id.recyclerView)
        brandsRecyclerView = view.findViewById(R.id.brandsRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        progressBar = view.findViewById(R.id.progressBar)

        // Установка GridLayoutManager для отображения двух товаров в строке
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        brandsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val productAdapter = ProductAdapter(requireContext(), filteredProducts,
            { product -> onProductClick(product) },
            { product -> addToCart(product) }
        )
        recyclerView.adapter = productAdapter

        loadProducts()
        loadBrands()

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

        // Показываем ProgressBar перед началом загрузки
        progressBar.visibility = View.VISIBLE

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
                (recyclerView.adapter as ProductAdapter).notifyDataSetChanged()

                // Скрываем ProgressBar после загрузки
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка загрузки продуктов: ${exception.message}", Toast.LENGTH_SHORT).show()
                // Скрываем ProgressBar в случае ошибки
                progressBar.visibility = View.GONE
            }
    }

    private fun loadBrands() {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("brand")
            .get()
            .addOnSuccessListener { result ->
                brands.clear() // Очищаем старые данные

                for (document in result) {
                    val brand = document.toObject(BrandDB::class.java)
                    brands.add(brand)
                }

                // Инициализируем адаптер для брендов
                brandAdapter = BrandAdapter(requireContext(), brands) { brand -> onBrandClick(brand) }
                brandsRecyclerView.adapter = brandAdapter // Установка адаптера для brandsRecyclerView
                brandAdapter.notifyDataSetChanged() // Обновляем данные в адаптере
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка загрузки брендов: ${exception.message}", Toast.LENGTH_SHORT).show()
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
        (recyclerView.adapter as ProductAdapter).notifyDataSetChanged()
    }

    private fun onProductClick(product: ProductDB) {
        product.views += 1
        // Call the new HistoryManager to add the product ID to the history
        HistoryManager.addToViewHistory(requireContext(), product.id)

        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        findNavController().navigate(R.id.action_navigation_home_to_productDetailFragment, bundle)
    }



    private fun onBrandClick(brand: BrandDB) {
        // Обработка клика по бренду
        Toast.makeText(context, "Вы выбрали бренд: ${brand.name}", Toast.LENGTH_SHORT).show()
    }


    private fun addToCart(product: ProductDB) {
        CartManager.addToCart(requireContext(), product)
    }
}
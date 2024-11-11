package com.example.zedkashop.ui.product

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.example.zedkashop.ui.cart.CartManager
import com.google.firebase.firestore.FirebaseFirestore

class ProductCatalogFragment : Fragment(R.layout.fragment_product_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<ProductDB>()
    private lateinit var sortSpinner: Spinner
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Массив с вариантами сортировки
    private val sortOptions = arrayOf(
        "Не выбрано",
        "Сортировать по цене (возр.)",
        "Сортировать по цене (уб.)",
        "Сортировать по просмотрам (возр.)",
        "Сортировать по просмотрам (уб.)",
        "Сортировать по покупкам (возр.)",
        "Сортировать по покупкам (уб.)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка заголовка ActionBar при создании фрагмента
        val category = arguments?.getString("category") ?: "Каталог"
        setActionBarTitle(category) // Установка заголовка ActionBar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Установить кнопку "Назад" в ActionBar
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sortSpinner = view.findViewById(R.id.sortSpinner)
        setupSpinner()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Передаем необходимые параметры в ProductAdapter
        productAdapter = ProductAdapter(
            requireContext(),
            productList,
            { product ->
                // Log product click and navigate to detail fragment
                Log.d("ProductCatalogFragment", "Product clicked: ${product.name}")
                val bundle = Bundle().apply { putSerializable("product", product) }
                findNavController().navigate(R.id.action_productCatalogFragment2_to_productDetailFragment, bundle)
            },
            { product -> addToCart(product) },
            { /* No specific action needed for onShowDetailsClick */ }
        )


        recyclerView.adapter = productAdapter
        loadProductsByCategory(arguments?.getString("category") ?: "")
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sortProducts(position) // Вызов функции для сортировки продуктов по выбранному критерию
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })
    }

    private fun loadProductsByCategory(category: String) {
        firestore.collection("products")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    val product = document.toObject(ProductDB::class.java)
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun sortProducts(criteria: Int) {
        when (criteria) {
            0 -> productList.sortBy { it.price.toDoubleOrNull() ?: 0.0 } // Сортировка по цене (возр.)
            1 -> productList.sortByDescending { it.price.toDoubleOrNull() ?: 0.0 } // Сортировка по цене (уб.)
            2 -> productList.sortBy { it.views } // Сортировка по просмотрам (возр.)
            3 -> productList.sortByDescending { it.views } // Сортировка по просмотрам (уб.)
            4 -> productList.sortBy { it.purchases } // Сортировка по покупкам (возр.)
            5 -> productList.sortByDescending { it.purchases } // Сортировка по покупкам (уб.)
        }
        productAdapter.notifyDataSetChanged() // Уведомление адаптера о изменении данных
    }

    private fun addToCart(product: ProductDB) {
        CartManager.addToCart(requireContext(), product)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Обработка нажатия на кнопку "Назад"
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBarTitle(title: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }
}
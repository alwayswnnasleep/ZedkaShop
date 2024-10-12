package com.example.zedkashop.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ProductCatalogFragment : Fragment(R.layout.fragment_product_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<ProductDB>()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        productAdapter = ProductAdapter(productList) { product ->
            // Логирование клика на продукт
            Log.d("ProductCatalogFragment", "Product clicked: ${product.name}")

            // Создание Bundle для передачи данных о продукте
            val bundle = Bundle().apply {
                putSerializable("product", product) // Передаем продукт как Serializable
            }

            // Переход к детальному фрагменту с передачей данных
            findNavController().navigate(R.id.action_productCatalogFragment2_to_productDetailFragment, bundle)
        }

        recyclerView.adapter = productAdapter
        loadProductsByCategory(arguments?.getString("category") ?: "")
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
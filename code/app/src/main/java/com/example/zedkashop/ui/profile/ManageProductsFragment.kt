package com.example.zedkashop.ui.manageproducts

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.home.ProductAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageProductsFragment : Fragment(R.layout.fragment_history) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ManageProductAdapter // Use the new adapter

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<ProductDB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "Управление товарами"

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productAdapter = ManageProductAdapter(
            requireContext(),
            productList,
            { product -> navigateToProductDetail(product) }, // onProductClick
            { product -> manageProduct(product) } // onManageButtonClick
        )

        recyclerView.adapter = productAdapter
        loadUserProducts()
        setupBackButton()
    }


    private fun navigateToProductDetail(product: ProductDB) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }
        findNavController().navigate(R.id.action_manageProductsFragment_to_productDetailFragment, bundle)
    }

    private fun loadUserProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val productsRef = firestore.collection("users").document(userId).collection("products")

        productsRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d("ManageProductsFragment", "Нет товаров для отображения")
                return@addOnSuccessListener
            }
            productList.clear()
            snapshot.documents.forEach { document ->
                val productId = document.getString("productId") ?: return@forEach
                loadProduct(productId)
            }
        }.addOnFailureListener { e ->
            Log.e("ManageProductsFragment", "Ошибка при загрузке товаров: $e")
        }
    }

    private fun loadProduct(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { productDoc ->
                val product = productDoc.toObject(ProductDB::class.java)
                product?.let {
                    val viewsCount = productDoc.getLong("views") ?: 0
                    it.views = viewsCount.toInt()

                    productList.add(it)
                    productAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener { e ->
                Log.e("ManageProductsFragment", "Ошибка при загрузке продукта: $e")
            }
    }

    internal fun manageProduct(product: ProductDB) {
       showManageProductDialog(product)
    }

    private fun showManageProductDialog(product: ProductDB) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_manage_product, null)
        dialog.setContentView(dialogView)

        dialogView.findViewById<View>(R.id.buttonEdit).setOnClickListener {
            dialog.dismiss()
           navigateToEditProduct(product) // Переход к редактированию
        }

        dialogView.findViewById<View>(R.id.buttonDelete).setOnClickListener {
            dialog.dismiss()
            deleteProduct(product) // Удаление продукта
        }

        dialog.show()
    }

    private fun navigateToEditProduct(product: ProductDB) {
        val bundle = Bundle().apply {
            putSerializable("product", product) // Передаем продукт для редактирования
        }

    }

    private fun deleteProduct(product: ProductDB) {
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Reference to the product document in the 'products' collection
        val productRef = firestore.collection("products").document(product.id)

        // Delete the product document
        productRef.delete().addOnSuccessListener {
            Log.d("ManageProductsFragment", "Продукт успешно удален из коллекции products")

            // Optionally, also remove it from the user's products collection
            val userProductRef = firestore.collection("users").document(userId).collection("products").document(product.id)
            userProductRef.delete().addOnSuccessListener {
                Log.d("ManageProductsFragment", "Продукт успешно удален из коллекции пользователя")
            }.addOnFailureListener { e ->
                Log.e("ManageProductsFragment", "Ошибка при удалении продукта из коллекции пользователя: $e")
            }

            // Remove the product from the local list and notify the adapter
            productList.remove(product)
            productAdapter.notifyDataSetChanged() // Update the UI
        }.addOnFailureListener { e ->
            Log.e("ManageProductsFragment", "Ошибка при удалении продукта из коллекции products: $e")
            // Notify the user about the failure
            Snackbar.make(requireView(), "Ошибка при удалении продукта", Snackbar.LENGTH_SHORT).show()
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
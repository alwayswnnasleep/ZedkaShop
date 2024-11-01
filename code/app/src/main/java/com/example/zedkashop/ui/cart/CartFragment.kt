package com.example.zedkashop.ui.cart

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
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
    private val productQuantities = mutableMapOf<String, Int>()
    private var totalPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "Корзина"

        // Инициализация элементов интерфейса
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

        setupSwipeToDelete()
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
                buyButton.text = "Купить" // Сброс текста кнопки
                return@addOnSuccessListener
            }

            totalPrice = 0.0
            productList.clear()
            productQuantities.clear()

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
                        val quantity = productQuantities[product.id] ?: 1
                        productQuantities[product.id] = quantity
                        totalPrice += (parsePrice(it.price) ?: 0.0) * quantity
                        updateTotalPriceDisplay()
                        productAdapter.notifyDataSetChanged()
                    } ?: Log.e("CartFragment", "Продукт не найден для ID: $productId")
                }.addOnFailureListener { e ->
                    Log.e("CartFragment", "Ошибка при загрузке продукта: $e")
                }
        }
    }

    private fun parsePrice(price: String): Double? {
        return price.replace("₽", "").replace(",", ".").trim().toDoubleOrNull()
    }

    private fun onQuantityChange(product: ProductDB, newQuantity: Int) {
        productQuantities[product.id] = newQuantity
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        totalPrice = productList.sumOf { product ->
            (parsePrice(product.price) ?: 0.0) * (productQuantities[product.id] ?: 1)
        }
        updateTotalPriceDisplay()
    }

    private fun updateTotalPriceDisplay() {
        totalPriceTextView.text = "Общая стоимость: ${totalPrice} ₽"
        buyButton.text = "Купить за ${totalPrice} ₽" // Обновляем текст кнопки
    }

    private fun purchaseProducts() {
        Log.d("CartFragment", "Покупка товаров: $totalPrice")
        // Логика для обработки покупки
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val product = productList[position]
                removeProductFromCart(product, position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0) { // Свайп влево
                    val itemView = viewHolder.itemView
                    val background = ColorDrawable(Color.RED)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    val originalIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                    val iconWidth = (originalIcon?.intrinsicWidth ?: 0) / 3
                    val iconHeight = (originalIcon?.intrinsicHeight ?: 0) / 3
                    val resizedIcon = Bitmap.createScaledBitmap(
                        originalIcon?.toBitmap() ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                        iconWidth,
                        iconHeight,
                        false
                    )

                    val iconMargin = (itemView.height - resizedIcon.height) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + resizedIcon.height
                    val iconLeft = itemView.right - resizedIcon.width - iconMargin - 20
                    val iconRight = itemView.right - iconMargin - 20

                    c.drawBitmap(resizedIcon, iconLeft.toFloat(), iconTop.toFloat(), null)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView)
    }

    private fun removeProductFromCart(product: ProductDB, position: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = firestore.collection("users").document(userId).collection("cart").document(product.id)

        cartRef.delete().addOnSuccessListener {
            productList.removeAt(position)
            productAdapter.notifyItemRemoved(position)
            updateTotalPrice()
        }.addOnFailureListener { e ->
            Log.e("CartFragment", "Ошибка при удалении товара: $e")
        }
    }
}

// Расширение для конвертации Drawable в Bitmap
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
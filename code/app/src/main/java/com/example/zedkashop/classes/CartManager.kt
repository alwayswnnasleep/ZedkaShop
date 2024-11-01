package com.example.zedkashop.ui.cart

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.zedkashop.data.ProductDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object CartManager {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addToCart(context: Context, product: ProductDB) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        val cartRef = firestore.collection("users").document(userId).collection("cart").document(product.id)

        cartRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentQuantity = document.getLong("quantity") ?: 0
                cartRef.update("quantity", currentQuantity + 1)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Количество товара обновлено", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("CartManager", "Ошибка при обновлении количества в корзине", e)
                    }
            } else {
                cartRef.set(mapOf("productId" to product.id, "quantity" to 1))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("CartManager", "Ошибка при добавлении в корзину", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("CartManager", "Ошибка при проверке наличия в корзине", e)
        }
    }
}
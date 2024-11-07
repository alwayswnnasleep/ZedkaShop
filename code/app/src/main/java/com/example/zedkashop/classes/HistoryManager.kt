package com.example.zedkashop.ui.history

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object HistoryManager {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Добавление продукта в историю просмотров
    fun addToViewHistory(context: Context, productId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        val historyRef = firestore.collection("users").document(userId).collection("history_view").document(productId)

        // Проверяем, существует ли продукт в истории просмотров
        historyRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Добавляем ID продукта в историю просмотров
                historyRef.set(mapOf("productId" to productId))
                    .addOnSuccessListener {
                        Log.d("HistoryManager", "Товар добавлен в историю просмотров: $productId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HistoryManager", "Ошибка при добавлении в историю просмотров", e)
                    }
            } else {
                // Опционально обновляем время последнего просмотра
                historyRef.update("lastViewed", System.currentTimeMillis())
                    .addOnSuccessListener {
                        Log.d("HistoryManager", "Время последнего просмотра обновлено: $productId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HistoryManager", "Ошибка при обновлении истории просмотров", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("HistoryManager", "Ошибка при проверке истории просмотров", e)
        }
    }

    // Добавление продукта в историю покупок
    fun addToPurchaseHistory(context: Context, productId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
            return
        }

        val historyRef = firestore.collection("users").document(userId).collection("history_purchase").document(productId)

        // Проверяем, существует ли ID продукта в истории покупок
        historyRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Добавляем ID продукта в историю покупок
                historyRef.set(mapOf("productId" to productId))
                    .addOnSuccessListener {
                        Log.d("HistoryManager", "Товар добавлен в историю покупок: $productId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HistoryManager", "Ошибка при добавлении в историю покупок", e)
                    }
            } else {
                // Опционально обновляем время последней покупки, если это необходимо
                historyRef.update("lastPurchased", System.currentTimeMillis())
                    .addOnSuccessListener {
                        Log.d("HistoryManager", "Время последней покупки обновлено: $productId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HistoryManager", "Ошибка при обновлении истории покупок", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("HistoryManager", "Ошибка при проверке истории покупок", e)
        }
    }
}
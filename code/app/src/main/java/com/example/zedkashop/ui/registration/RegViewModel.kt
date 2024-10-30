package com.example.zedkashop.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Обновлённый метод signUp с именем пользователя
    fun signUp(name: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Регистрация успешна, получаем UID нового пользователя
                    val userId = mAuth.currentUser?.uid
                    // Создаем документ в Firestore
                    if (userId != null) {
                        val userData = hashMapOf(
                            "name" to name, // Добавлено поле имени
                            "email" to email,
                            "profilePhoto" to "", // Здесь можно добавить URL фото профиля
                            "cart" to emptyList<String>(), // Пустая корзина
                            "viewHistory" to emptyList<String>() // Пустая история просмотров
                        )

                        db.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                // Данные успешно добавлены в Firestore
                                callback(true, "Регистрация успешна!")
                            }
                            .addOnFailureListener { e ->
                                // Ошибка при добавлении данных
                                callback(false, "Ошибка при добавлении данных: ${e.message}")
                            }
                    } else {
                        callback(false, "Не удалось получить UID пользователя.")
                    }
                } else {
                    // Ошибка регистрации
                    callback(false, "Ошибка регистрации: ${task.exception?.message}")
                }
            }
    }
}
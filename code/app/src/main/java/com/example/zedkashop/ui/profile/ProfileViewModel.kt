package com.example.zedkashop.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // LiveData для имени пользователя
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    fun signOut() {
        auth.signOut()
    }

    fun loadUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Получаем имя из документа
                        val name = document.getString("name")
                        _userName.value = name ?: "Нет имени"
                    } else {
                        Log.d("ProfileViewModel", "Документ не найден")
                        _userName.value = "Нет имени"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileViewModel", "Ошибка получения имени: $e")
                    _userName.value = "Нет имени"
                }
        } else {
            _userName.value = "Нет имени"
        }
    }
}
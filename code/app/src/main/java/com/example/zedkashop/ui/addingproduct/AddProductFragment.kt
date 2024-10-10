package com.example.zedkashop.ui.addingproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.ViewModelProvider
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB

class AddProductFragment : Fragment() {
    private lateinit var viewModel: AddProductViewModel
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        val placePhoto: ImageView = view.findViewById(R.id.placePhoto)
        val enterName: EditText = view.findViewById(R.id.enterName)
        val enterPrice: EditText = view.findViewById(R.id.enterPrice)
        val enterDescription: EditText = view.findViewById(R.id.enterDescription)
        val chooseConsumer: Spinner = view.findViewById(R.id.chooseConsumer)
        val addButton: ImageView = view.findViewById(R.id.addProudct)

        addButton.setOnClickListener {
            val productName = enterName.text.toString()
            val productPrice = enterPrice.text.toString()
            val productDescription = enterDescription.text.toString()
            val selectedConsumer = chooseConsumer.selectedItem?.toString() ?: "Неизвестный потребитель" // Обработка null

            val product = ProductDB(
                name = productName,
                price = productPrice,
                description = productDescription,
                imageUrl = "", // Укажите URL изображения, если доступно
                rating = "", // Укажите рейтинг, если доступно
                consumer = selectedConsumer,
                category = "" // Укажите категорию, если доступно
            )

            firestore.collection("products")
                .document()
                .set(product)
                .addOnSuccessListener {
                    // Успешно добавлено
                }
                .addOnFailureListener { e ->
                    // Обработка ошибок
                }
        }

        return view
    }
}
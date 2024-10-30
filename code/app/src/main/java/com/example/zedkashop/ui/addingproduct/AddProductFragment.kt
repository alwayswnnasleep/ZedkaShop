package com.example.zedkashop.ui.addingproduct

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.ui.base.BaseFragment
import java.util.UUID

class AddProductFragment : BaseFragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null // Изменен тип на Uri? для допуска значения null
    private lateinit var placePhoto: ImageView
    private lateinit var view: View // Добавляем переменную для хранения ссылки на представление
    override fun onResume() {
        super.onResume()
        setActionBarTitle("Добавление товара") // Устанавливаем заголовок
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_add_product, container, false) // Инициализация представления

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        placePhoto = view.findViewById(R.id.placePhoto)
        val enterName: EditText = view.findViewById(R.id.enterName)
        val enterPrice: EditText = view.findViewById(R.id.enterPrice)
        val enterDescription: EditText = view.findViewById(R.id.enterDescription)
        val chooseManufacturer: Spinner = view.findViewById(R.id.chooseManufacturer)
        val chooseCategory: Spinner = view.findViewById(R.id.chooseCategory)
        val addButton: Button = view.findViewById(R.id.addProduct)

        // Установка клика на placePhoto для открытия галереи
        placePhoto.setOnClickListener {
            openGallery()
        }

        addButton.setOnClickListener {
            // Логика добавления продукта
            val productName = enterName.text.toString()
            val productPrice = enterPrice.text.toString()
            val productDescription = enterDescription.text.toString()
            val selectedConsumer = chooseManufacturer.selectedItem.toString()
            val selectedCategory = chooseCategory.selectedItem.toString()

            // Проверка, что все поля заполнены и изображение выбрано
            if (productName.isNotEmpty() && productPrice.isNotEmpty() && productDescription.isNotEmpty() && selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri!!, selectedCategory, productName, productPrice, productDescription, selectedConsumer) // Используем !! для безопасного извлечения
            } else {
                // Обработка случая, если поля не заполнены
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля и выберите изображение.", Toast.LENGTH_SHORT).show()
            }
        }

        setupSpinners()

        return view
    }

    private fun setupSpinners() {
        // Настройка Spinner для производителей
        val manufacturers = arrayOf("ZedkaShop", "Lowa", "Mil-Tec")
        val manufacturerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, manufacturers)
        manufacturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val chooseManufacturer: Spinner = view.findViewById(R.id.chooseManufacturer)
        chooseManufacturer.adapter = manufacturerAdapter

        // Настройка Spinner для категорий
        val categories = arrayOf("Шлем", "Жилет", "Одежда", "Разгрузочная система", "Подсумок", "Обувь")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val chooseCategory: Spinner = view.findViewById(R.id.chooseCategory)
        chooseCategory.adapter = categoryAdapter
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImageUri = data.data // Получаем Uri выбранного изображения
            placePhoto.setImageURI(selectedImageUri) // Отображаем изображение в ImageView
        } else {
            // Обработка случая, если изображение не выбрано
            Toast.makeText(requireContext(), "Ошибка выбора изображения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, category: String, productName: String, productPrice: String, productDescription: String, selectedConsumer: String) {
        val fileName = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("products/$category/$fileName.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val product = ProductDB(
                        name = productName,
                        price = productPrice,
                        description = productDescription,
                        imageUrl = uri.toString(),
                        consumer = selectedConsumer,
                        category = category
                    )

                    // Создаем новый документ с уникальным ID
                    val productRef = firestore.collection("products").document()
                    productRef.set(product.copy(id = productRef.id)) // Сохраняем ID продукта
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Продукт успешно добавлен!", Toast.LENGTH_SHORT).show()
                            clearInputs() // Очищаем поля после добавления
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Ошибка добавления продукта: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Ошибка загрузки изображения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        // Очищаем поля ввода и изображение
        view.findViewById<EditText>(R.id.enterName).text.clear()
        view.findViewById<EditText>(R.id.enterPrice).text.clear()
        view.findViewById<EditText>(R.id.enterDescription).text.clear()
        placePhoto.setImageURI(null) // Сброс изображения
        selectedImageUri = null // Обнуляем URI
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
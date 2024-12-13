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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import java.util.UUID

class AddProductFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var placePhoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        try {
            firestore = FirebaseFirestore.getInstance()
            storage = FirebaseStorage.getInstance()

            placePhoto = view.findViewById<ImageView>(R.id.placePhoto)
            val enterName: EditText = view.findViewById(R.id.enterName)
            val enterPrice: EditText = view.findViewById(R.id.enterPrice)
            val enterDescription: EditText = view.findViewById(R.id.enterDescription)
            val chooseManufacturer: Spinner = view.findViewById(R.id.chooseManufacturer)
            val chooseCategory: Spinner = view.findViewById(R.id.chooseCategory)
            val addButton: Button = view.findViewById(R.id.addProduct)

            setupSpinners(view)

            placePhoto.setOnClickListener {
                openGallery()
            }

            addButton.setOnClickListener {
                val productName = enterName.text.toString()
                val productPrice = enterPrice.text.toString().trim()
                val productDescription = enterDescription.text.toString()
                val selectedConsumer = chooseManufacturer.selectedItem?.toString() ?: ""
                val selectedCategory = chooseCategory.selectedItem?.toString() ?: ""

                if (productName.isNotEmpty() && productPrice.isNotEmpty() && productDescription.isNotEmpty() && selectedImageUri != null) {
                    val formattedPrice = "$productPrice ₽"
                    uploadImageToFirebase(selectedImageUri!!, selectedCategory, productName, formattedPrice, productDescription, selectedConsumer)
                } else {
                    Toast.makeText(requireContext(), "Пожалуйста, заполните все поля и выберите изображение.", Toast.LENGTH_SHORT).show()
                }
            }

            (activity as AppCompatActivity).supportActionBar?.title = "Добавить товар"

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка инициализации: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupSpinners(view: View) {
        val categories = arrayOf("Шлем", "Бронежилет", "Одежда", "Разгрузочная система", "Подсумок", "Обувь")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val chooseCategory: Spinner = view.findViewById(R.id.chooseCategory)
        chooseCategory.adapter = categoryAdapter

        val manufacturersMap = mapOf(
            "Шлем" to arrayOf("FAST", "Omnitek"),
            "Бронежилет" to arrayOf("РусАрм"),
            "Одежда" to arrayOf("EmersonGear", "PROPPER BDU", "Yakeda"),
            "Разгрузочная система" to arrayOf("Модель 1", "Модель 2"),
            "Подсумок" to arrayOf("Модель A", "Модель B"),
            "Обувь" to arrayOf("Lowa", "Salomon")
        )

        val chooseManufacturer: Spinner = view.findViewById(R.id.chooseManufacturer)

        chooseCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                val manufacturers = manufacturersMap[selectedCategory] ?: emptyArray()
                val manufacturerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, manufacturers)
                manufacturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                chooseManufacturer.adapter = manufacturerAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Ничего не делаем
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            placePhoto.setImageURI(selectedImageUri)
        } else {
            Toast.makeText(requireContext(), "Ошибка выбора изображения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(
        imageUri: Uri,
        category: String,
        productName: String,
        productPrice: String,
        productDescription: String,
        selectedConsumer: String
    ) {
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

                    val productRef = firestore.collection("products").document()
                    productRef.set(product.copy(id = productRef.id))
                        .addOnSuccessListener {
                            // Добавление ID товара пользователю
                            addProductToUser(productRef.id)
                            Toast.makeText(requireContext(), "Продукт успешно добавлен!", Toast.LENGTH_SHORT).show()
                            clearInputs()
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

    private fun addProductToUser(productId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userProductsRef = firestore.collection("users").document(userId).collection("products").document(productId)

        userProductsRef.set(mapOf("productId" to productId))
            .addOnSuccessListener {
                // Опционально обработать успешное добавление
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Ошибка добавления товара пользователю: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        val view = requireView() // Получаем текущий view
        view.findViewById<EditText>(R.id.enterName).text.clear()
        view.findViewById<EditText>(R.id.enterPrice).text.clear()
        view.findViewById<EditText>(R.id.enterDescription).text.clear()
        placePhoto.setImageURI(null)
        selectedImageUri = null
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
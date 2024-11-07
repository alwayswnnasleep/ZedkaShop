package com.example.zedkashop.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.databinding.FragmentProductDetailBinding
import com.example.zedkashop.ui.cart.CartManager

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        // Установка заголовка тулбара
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title = "Детали продукта"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Получение продукта из аргументов
        val product = arguments?.getSerializable("product") as? ProductDB

        product?.let {
            binding.productName.text = it.name
            binding.productPrice.text = it.price
            binding.productDescription.text = it.description
            binding.productCategory.text = it.category
            binding.productConsumer.text = it.consumer // Убедитесь, что это поле существует

            // Загрузка изображения продукта
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.productImage)

            // Настройка кнопки добавления в корзину
            binding.addToCartButton.setOnClickListener {

                CartManager.addToCart(requireContext(), product)
            }
        } ?: Log.e("ProductDetailFragment", "Product data is null")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Обработка нажатия на кнопку "Назад"
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
package com.example.zedkashop.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zedkashop.R
import com.example.zedkashop.data.ProductDB
import com.example.zedkashop.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        val product = arguments?.getSerializable("product") as? ProductDB

        product?.let {
            binding.productName.text = it.name
            binding.productPrice.text = it.price
            binding.productDescription.text = it.description
            binding.productCategory.text = it.category

            // Установка изображения товара
            Glide.with(this)
                .load(it.imageUrl) // Предполагается, что у вас есть поле imageUrl в ProductDB
                .into(binding.productImage)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
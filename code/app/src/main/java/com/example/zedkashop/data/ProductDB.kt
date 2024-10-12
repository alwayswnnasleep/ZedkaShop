package com.example.zedkashop.data

import java.io.Serializable

data class ProductDB(
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val consumer: String = "",
    val category: String = "",
    var views: Int = 0
) : Serializable
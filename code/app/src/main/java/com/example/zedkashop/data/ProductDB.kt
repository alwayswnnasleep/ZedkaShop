package com.example.zedkashop.data

import java.io.Serializable
import java.util.UUID

data class ProductDB(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val consumer: String = "",
    val category: String = "",
    var views: Int = 0,
    var purchases: Int = 0
) : Serializable
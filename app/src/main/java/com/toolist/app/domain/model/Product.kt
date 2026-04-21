package com.toolist.app.domain.model

data class Product(
    val id: String,
    val listId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val categoryId: String?,
    val categoryName: String?,
    val estimatedPrice: Double,
    val status: ProductStatus,
    val notes: String?,
    val imageUrl: String?,
)

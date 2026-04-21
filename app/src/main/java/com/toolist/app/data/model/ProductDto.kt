package com.toolist.app.data.model

import com.google.firebase.firestore.DocumentSnapshot
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus

data class ProductDto(
    val id: String = "",
    val listId: String = "",
    val name: String = "",
    val quantity: Double = 1.0,
    val unit: String = "Unidad",
    val categoryId: String? = null,
    val categoryName: String? = null,
    val estimatedPrice: Double = 0.0,
    val status: String = ProductStatus.PENDING.name,
    val notes: String? = null,
    val imageUrl: String? = null,
) {

    fun toProduct(): Product = Product(
        id = id,
        listId = listId,
        name = name,
        quantity = quantity,
        unit = unit,
        categoryId = categoryId,
        categoryName = categoryName,
        estimatedPrice = estimatedPrice,
        status = runCatching { ProductStatus.valueOf(status) }.getOrDefault(ProductStatus.PENDING),
        notes = notes,
        imageUrl = imageUrl,
    )

    fun toMap(): Map<String, Any?> = mapOf(
        "listId" to listId,
        "name" to name,
        "quantity" to quantity,
        "unit" to unit,
        "categoryId" to categoryId,
        "categoryName" to categoryName,
        "estimatedPrice" to estimatedPrice,
        "status" to status,
        "notes" to notes,
        "imageUrl" to imageUrl,
    )

    companion object {

        fun fromDocument(doc: DocumentSnapshot, listId: String): ProductDto = ProductDto(
            id = doc.id,
            listId = listId,
            name = doc.getString("name") ?: "",
            quantity = doc.getDouble("quantity") ?: 1.0,
            unit = doc.getString("unit") ?: "Unidad",
            categoryId = doc.getString("categoryId"),
            categoryName = doc.getString("categoryName"),
            estimatedPrice = doc.getDouble("estimatedPrice") ?: 0.0,
            status = doc.getString("status") ?: ProductStatus.PENDING.name,
            notes = doc.getString("notes"),
            imageUrl = doc.getString("imageUrl"),
        )

        fun fromProduct(product: Product): ProductDto = ProductDto(
            id = product.id,
            listId = product.listId,
            name = product.name,
            quantity = product.quantity,
            unit = product.unit,
            categoryId = product.categoryId,
            categoryName = product.categoryName,
            estimatedPrice = product.estimatedPrice,
            status = product.status.name,
            notes = product.notes,
            imageUrl = product.imageUrl,
        )
    }
}

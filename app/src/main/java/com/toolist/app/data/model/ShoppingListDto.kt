package com.toolist.app.data.model

import com.google.firebase.firestore.DocumentSnapshot
import com.toolist.app.domain.model.ShoppingList

data class ShoppingListDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val colorHex: String = "#16A34A",
    val totalEstimated: Double = 0.0,
    val purchasedCount: Int = 0,
    val totalCount: Int = 0,
    val createdAt: Long = 0L,
) {

    fun toShoppingList(): ShoppingList = ShoppingList(
        id = id,
        name = name,
        description = description,
        colorHex = colorHex,
        totalEstimated = totalEstimated,
        purchasedCount = purchasedCount,
        totalCount = totalCount,
        createdAt = createdAt,
    )

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "description" to description,
        "colorHex" to colorHex,
        "totalEstimated" to totalEstimated,
        "purchasedCount" to purchasedCount,
        "totalCount" to totalCount,
        "createdAt" to createdAt,
    )

    companion object {

        fun fromDocument(doc: DocumentSnapshot): ShoppingListDto = ShoppingListDto(
            id = doc.id,
            name = doc.getString("name") ?: "",
            description = doc.getString("description") ?: "",
            colorHex = doc.getString("colorHex") ?: "#16A34A",
            totalEstimated = doc.getDouble("totalEstimated") ?: 0.0,
            purchasedCount = (doc.getLong("purchasedCount") ?: 0L).toInt(),
            totalCount = (doc.getLong("totalCount") ?: 0L).toInt(),
            createdAt = doc.getLong("createdAt") ?: 0L,
        )

        fun fromShoppingList(list: ShoppingList): ShoppingListDto = ShoppingListDto(
            id = list.id,
            name = list.name,
            description = list.description,
            colorHex = list.colorHex,
            totalEstimated = list.totalEstimated,
            purchasedCount = list.purchasedCount,
            totalCount = list.totalCount,
            createdAt = list.createdAt,
        )
    }
}

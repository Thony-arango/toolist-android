package com.toolist.app.data.model

import com.google.firebase.firestore.DocumentSnapshot
import com.toolist.app.domain.model.Category

data class CategoryDto(
    val id: String = "",
    val name: String = "",
    val icon: String = "📦",
) {
    fun toCategory(productCount: Int = 0): Category = Category(
        id = id,
        name = name,
        icon = icon,
        isSystem = false,
        productCount = productCount,
    )

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "icon" to icon,
    )

    companion object {
        fun fromDocument(doc: DocumentSnapshot): CategoryDto = CategoryDto(
            id = doc.id,
            name = doc.getString("name") ?: "",
            icon = doc.getString("icon") ?: "📦",
        )
    }
}

package com.toolist.app.domain.model

data class ShoppingList(
    val id: String,
    val name: String,
    val description: String,
    val colorHex: String,
    val totalEstimated: Double,
    val purchasedCount: Int,
    val totalCount: Int,
    val createdAt: Long,
) {
    val progress: Float
        get() = if (totalCount == 0) 0f else purchasedCount.toFloat() / totalCount.toFloat()

    val isCompleted: Boolean
        get() = totalCount > 0 && purchasedCount == totalCount
}

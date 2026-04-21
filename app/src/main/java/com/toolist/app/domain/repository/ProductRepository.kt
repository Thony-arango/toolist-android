package com.toolist.app.domain.repository

import com.toolist.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    /** Observa en tiempo real los productos de una lista. */
    fun observeProducts(listId: String): Flow<List<Product>>

    /** Agrega un producto y actualiza totalCount de la lista. Retorna el ID generado. */
    suspend fun addProduct(product: Product): Result<String>

    /** Actualiza los datos de un producto existente. */
    suspend fun updateProduct(product: Product): Result<Unit>

    /** Elimina un producto y actualiza los contadores de la lista. */
    suspend fun deleteProduct(listId: String, productId: String, wasPurchased: Boolean): Result<Unit>

    /** Alterna el estado PENDING ↔ PURCHASED y actualiza purchasedCount de la lista. */
    suspend fun toggleProductStatus(product: Product): Result<Unit>

    /** Mueve un producto a otra lista. */
    suspend fun moveProduct(product: Product, targetListId: String): Result<Unit>

    /** Crea una copia del producto (estado PENDING) en la misma lista. */
    suspend fun duplicateProduct(product: Product): Result<String>

    /** Pone todos los productos de la lista en PENDING y purchasedCount a 0. */
    suspend fun resetAllProducts(listId: String): Result<Unit>

    /** Recalcula totalEstimated sumando solo productos PENDING y lo persiste en la lista. */
    suspend fun recalculateTotalEstimated(listId: String): Result<Unit>
}

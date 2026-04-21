package com.toolist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.toolist.app.data.model.ProductDto
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.domain.repository.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ProductRepository {

    private fun listsCollection() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: "anonymous")
        .collection("lists")

    private fun productsCollection(listId: String) =
        listsCollection().document(listId).collection("products")

    override fun observeProducts(listId: String): Flow<List<Product>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = firestore
                .collection("users").document(uid)
                .collection("lists").document(listId)
                .collection("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) { trySend(emptyList()); return@addSnapshotListener }
                    val products = snapshot.documents.mapNotNull { doc ->
                        runCatching { ProductDto.fromDocument(doc, listId).toProduct() }.getOrNull()
                    }
                    trySend(products)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun addProduct(product: Product): Result<String> = runCatching {
        val dto = ProductDto.fromProduct(product)
        val ref = productsCollection(product.listId).add(dto.toMap()).await()
        listsCollection().document(product.listId)
            .update("totalCount", FieldValue.increment(1L)).await()
        ref.id
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        val dto = ProductDto.fromProduct(product)
        productsCollection(product.listId).document(product.id).set(dto.toMap()).await()
    }

    override suspend fun deleteProduct(
        listId: String,
        productId: String,
        wasPurchased: Boolean,
    ): Result<Unit> = runCatching {
        val batch = firestore.batch()
        batch.delete(productsCollection(listId).document(productId))
        val listUpdates = mutableMapOf<String, Any>("totalCount" to FieldValue.increment(-1L))
        if (wasPurchased) listUpdates["purchasedCount"] = FieldValue.increment(-1L)
        batch.update(listsCollection().document(listId), listUpdates)
        batch.commit().await()
    }

    override suspend fun toggleProductStatus(product: Product): Result<Unit> = runCatching {
        val newStatus = if (product.status == ProductStatus.PENDING) ProductStatus.PURCHASED else ProductStatus.PENDING
        val delta = if (newStatus == ProductStatus.PURCHASED) 1L else -1L
        val batch = firestore.batch()
        batch.update(productsCollection(product.listId).document(product.id), "status", newStatus.name)
        batch.update(listsCollection().document(product.listId), "purchasedCount", FieldValue.increment(delta))
        batch.commit().await()
    }

    override suspend fun moveProduct(product: Product, targetListId: String): Result<Unit> = runCatching {
        val targetRef = productsCollection(targetListId).document()
        val newDto = ProductDto.fromProduct(product.copy(id = targetRef.id, listId = targetListId, status = ProductStatus.PENDING))
        val batch = firestore.batch()
        batch.delete(productsCollection(product.listId).document(product.id))
        batch.set(targetRef, newDto.toMap())
        val sourceUpdates = mutableMapOf<String, Any>("totalCount" to FieldValue.increment(-1L))
        if (product.status == ProductStatus.PURCHASED) sourceUpdates["purchasedCount"] = FieldValue.increment(-1L)
        batch.update(listsCollection().document(product.listId), sourceUpdates)
        batch.update(listsCollection().document(targetListId), "totalCount", FieldValue.increment(1L))
        batch.commit().await()
    }

    override suspend fun duplicateProduct(product: Product): Result<String> = runCatching {
        val newRef = productsCollection(product.listId).document()
        val dto = ProductDto.fromProduct(product.copy(id = newRef.id, status = ProductStatus.PENDING))
        val batch = firestore.batch()
        batch.set(newRef, dto.toMap())
        batch.update(listsCollection().document(product.listId), "totalCount", FieldValue.increment(1L))
        batch.commit().await()
        newRef.id
    }

    override suspend fun resetAllProducts(listId: String): Result<Unit> = runCatching {
        val snapshot = productsCollection(listId).get().await()
        val batch = firestore.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "status", ProductStatus.PENDING.name)
        }
        batch.update(listsCollection().document(listId), "purchasedCount", 0L)
        batch.commit().await()
    }
}

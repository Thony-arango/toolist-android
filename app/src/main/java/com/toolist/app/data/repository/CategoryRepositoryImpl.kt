package com.toolist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.toolist.app.data.model.CategoryDto
import com.toolist.app.domain.model.Category
import com.toolist.app.domain.repository.CategoryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : CategoryRepository {

    private fun categoriesCollection() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: "anonymous")
        .collection("categories")

    override fun observeUserCategories(): Flow<List<Category>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())

        return callbackFlow {
            val listener = firestore
                .collection("users")
                .document(uid)
                .collection("categories")
                .orderBy("name")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val categories = snapshot.documents.mapNotNull { doc ->
                        runCatching { CategoryDto.fromDocument(doc).toCategory() }.getOrNull()
                    }
                    trySend(categories)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun createCategory(name: String, icon: String): Result<String> = runCatching {
        val dto = CategoryDto(name = name, icon = icon)
        val ref = categoriesCollection().add(dto.toMap()).await()
        ref.id
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        categoriesCollection().document(categoryId).delete().await()
    }
}

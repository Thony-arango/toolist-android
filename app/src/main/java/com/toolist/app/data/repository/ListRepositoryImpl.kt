package com.toolist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.toolist.app.data.model.ShoppingListDto
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.repository.ListRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ListRepository {

    // ── Colección del usuario autenticado ─────────────────────────────────

    private fun listsCollection() = firestore
        .collection("users")
        .document(auth.currentUser?.uid ?: "anonymous")
        .collection("lists")

    // ── Observar listas ───────────────────────────────────────────────────

    override fun observeLists(): Flow<List<ShoppingList>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())

        return callbackFlow {
            val listener = firestore
                .collection("users")
                .document(uid)
                .collection("lists")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val lists = snapshot.documents.mapNotNull { doc ->
                        runCatching { ShoppingListDto.fromDocument(doc).toShoppingList() }.getOrNull()
                    }
                    trySend(lists)
                }
            awaitClose { listener.remove() }
        }
    }

    // ── Observar una lista ────────────────────────────────────────────────

    override fun getList(listId: String): Flow<ShoppingList?> {
        val uid = auth.currentUser?.uid ?: return flowOf(null)

        return callbackFlow {
            val listener = firestore
                .collection("users")
                .document(uid)
                .collection("lists")
                .document(listId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) {
                        trySend(null)
                        return@addSnapshotListener
                    }
                    val list = runCatching {
                        ShoppingListDto.fromDocument(snapshot).toShoppingList()
                    }.getOrNull()
                    trySend(list)
                }
            awaitClose { listener.remove() }
        }
    }

    // ── Crear lista ───────────────────────────────────────────────────────

    override suspend fun createList(list: ShoppingList): Result<String> = runCatching {
        val dto = ShoppingListDto.fromShoppingList(list)
        val ref = listsCollection().add(dto.toMap()).await()
        ref.id
    }

    // ── Actualizar lista ──────────────────────────────────────────────────

    override suspend fun updateList(list: ShoppingList): Result<Unit> = runCatching {
        val dto = ShoppingListDto.fromShoppingList(list)
        listsCollection().document(list.id).set(dto.toMap()).await()
    }

    // ── Eliminar lista ────────────────────────────────────────────────────

    override suspend fun deleteList(listId: String): Result<Unit> = runCatching {
        // Borra los productos de la lista antes de borrar la lista misma
        val productsRef = listsCollection().document(listId).collection("products")
        val products = productsRef.get().await()
        val batch = firestore.batch()
        products.documents.forEach { batch.delete(it.reference) }
        batch.delete(listsCollection().document(listId))
        batch.commit().await()
    }
}

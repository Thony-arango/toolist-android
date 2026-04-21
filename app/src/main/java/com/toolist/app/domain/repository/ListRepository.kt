package com.toolist.app.domain.repository

import com.toolist.app.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    /** Observa en tiempo real todas las listas del usuario autenticado. */
    fun observeLists(): Flow<List<ShoppingList>>

    /** Observa en tiempo real una lista específica. Emite null si no existe. */
    fun getList(listId: String): Flow<ShoppingList?>

    /** Crea una nueva lista. Retorna el ID generado por Firestore. */
    suspend fun createList(list: ShoppingList): Result<String>

    /** Actualiza una lista existente. */
    suspend fun updateList(list: ShoppingList): Result<Unit>

    /** Elimina una lista y sus productos. */
    suspend fun deleteList(listId: String): Result<Unit>
}

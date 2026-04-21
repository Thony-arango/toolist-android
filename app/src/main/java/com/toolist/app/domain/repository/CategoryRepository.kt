package com.toolist.app.domain.repository

import com.toolist.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeUserCategories(): Flow<List<Category>>
    suspend fun createCategory(name: String, icon: String): Result<String>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
}

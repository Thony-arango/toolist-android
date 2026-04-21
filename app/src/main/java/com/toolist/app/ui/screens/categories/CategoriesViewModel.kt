package com.toolist.app.ui.screens.categories

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.R
import com.toolist.app.domain.model.Category
import com.toolist.app.domain.repository.CategoryRepository
import com.toolist.app.domain.usecase.list.GetListsUseCase
import com.toolist.app.domain.usecase.product.ObserveProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Definición de categorías del sistema (constantes internas)

private data class SystemCategoryDef(@StringRes val nameResId: Int, val icon: String)

private val SYSTEM_CATEGORIES = listOf(
    SystemCategoryDef(R.string.category_dairy,         "🥛"),
    SystemCategoryDef(R.string.category_meat,          "🥩"),
    SystemCategoryDef(R.string.category_vegetables,    "🥦"),
    SystemCategoryDef(R.string.category_fruits,        "🍎"),
    SystemCategoryDef(R.string.category_bakery,        "🍞"),
    SystemCategoryDef(R.string.category_cleaning,      "🧹"),
    SystemCategoryDef(R.string.category_personal_care, "🧴"),
    SystemCategoryDef(R.string.category_beverages,     "🥤"),
    SystemCategoryDef(R.string.category_snacks,        "🍿"),
    SystemCategoryDef(R.string.category_frozen,        "❄️"),
    SystemCategoryDef(R.string.category_other,         "📦"),
)

// UiState

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val userCategories: List<Category> = emptyList(),
    val systemCategories: List<Category> = emptyList(),
    val showCreateDialog: Boolean = false,
    val pendingDeleteCategory: Category? = null,
    val error: String? = null,
    val snackMessage: String? = null,
)

// ViewModel

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val getListsUseCase: GetListsUseCase,
    private val observeProductsUseCase: ObserveProductsUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val systemCategoryDefs: List<Category> = SYSTEM_CATEGORIES.mapIndexed { index, def ->
        Category(
            id = "sys_$index",
            name = context.getString(def.nameResId),
            icon = def.icon,
            isSystem = true,
            productCount = 0,
        )
    }

    init {
        observeCategoriesAndCounts()
    }

    private fun observeCategoriesAndCounts() {
        viewModelScope.launch {
            // Combina flujo de categorías del usuario con conteo de productos
            combine(
                categoryRepository.observeUserCategories(),
                allProductsFlow(),
            ) { userCats, allProducts ->
                val countsByName: Map<String, Int> = allProducts
                    .groupBy { it.categoryName ?: "" }
                    .mapValues { it.value.size }

                val userWithCounts = userCats.map { cat ->
                    cat.copy(productCount = countsByName[cat.name] ?: 0)
                }
                val systemWithCounts = systemCategoryDefs.map { cat ->
                    cat.copy(productCount = countsByName[cat.name] ?: 0)
                }

                CategoriesUiState(
                    isLoading = false,
                    userCategories = userWithCounts,
                    systemCategories = systemWithCounts,
                    showCreateDialog = _uiState.value.showCreateDialog,
                    pendingDeleteCategory = _uiState.value.pendingDeleteCategory,
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { newState -> _uiState.update { newState } }
        }
    }

    /** Combina todos los productos de todas las listas en un solo Flow. */
    @Suppress("OPT_IN_USAGE")
    private fun allProductsFlow() = getListsUseCase()
        .flatMapLatest { lists ->
            if (lists.isEmpty()) flowOf(emptyList())
            else combine(lists.map { observeProductsUseCase(it.id) }) { arrays ->
                arrays.flatMap { it.toList() }
            }
        }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun dismissCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun createCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            dismissCreateDialog()
            categoryRepository.createCategory(trimmed, icon = "📦")
                .onSuccess {
                    _uiState.update {
                        it.copy(snackMessage = context.getString(R.string.snack_category_created))
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun requestDelete(category: Category) {
        _uiState.update { it.copy(pendingDeleteCategory = category) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDeleteCategory = null) }
    }

    fun confirmDelete() {
        val cat = _uiState.value.pendingDeleteCategory ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            categoryRepository.deleteCategory(cat.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(snackMessage = context.getString(R.string.snack_category_deleted))
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSnack() {
        _uiState.update { it.copy(snackMessage = null) }
    }
}

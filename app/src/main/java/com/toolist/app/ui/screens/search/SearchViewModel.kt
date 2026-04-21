package com.toolist.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.usecase.list.GetListsUseCase
import com.toolist.app.domain.usecase.product.ObserveProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UiState
// ---------------------------------------------------------------------------

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val isSearching: Boolean = false,
    val error: String? = null,
)

data class SearchResult(
    val product: Product,
    val listName: String,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getListsUseCase: GetListsUseCase,
    private val observeProductsUseCase: ObserveProductsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Cache de todos los productos con nombre de lista para filtrado rápido
    private var allResults: List<SearchResult> = emptyList()

    init {
        observeAllProducts()
    }

    @Suppress("OPT_IN_USAGE")
    private fun observeAllProducts() {
        viewModelScope.launch {
            getListsUseCase()
                .flatMapLatest { lists ->
                    if (lists.isEmpty()) flowOf(emptyList<SearchResult>())
                    else combine(
                        lists.map { list ->
                            observeProductsUseCase(list.id).map { products ->
                                products.map { product -> SearchResult(product, list.name) }
                            }
                        }
                    ) { arrays -> arrays.flatMap { it.toList() } }
                }
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { results ->
                    allResults = results
                    val categories = results
                        .mapNotNull { it.product.categoryName }
                        .distinct()
                        .sorted()
                    _uiState.update { state ->
                        state.copy(
                            availableCategories = categories,
                            results = filterResults(
                                allResults = results,
                                query = state.query,
                                category = state.selectedCategory,
                            ),
                        )
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                query = query,
                isSearching = query.isNotEmpty(),
                results = filterResults(allResults, query, state.selectedCategory),
            )
        }
    }

    fun onSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            val updated = (listOf(trimmed) + state.recentSearches).distinct().take(10)
            state.copy(recentSearches = updated)
        }
    }

    fun selectRecentSearch(query: String) {
        onQueryChange(query)
        onSearch(query)
    }

    fun removeRecentSearch(query: String) {
        _uiState.update { it.copy(recentSearches = it.recentSearches - query) }
    }

    fun clearAllRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun selectCategoryFilter(category: String?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                results = filterResults(allResults, state.query, category),
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Filtrado ───────────────────────────────────────────────────────────

    private fun filterResults(
        allResults: List<SearchResult>,
        query: String,
        category: String?,
    ): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return allResults.filter { result ->
            val matchesQuery = result.product.name.lowercase().contains(q) ||
                result.product.categoryName?.lowercase()?.contains(q) == true ||
                result.listName.lowercase().contains(q)
            val matchesCategory = category == null || result.product.categoryName == category
            matchesQuery && matchesCategory
        }
    }
}

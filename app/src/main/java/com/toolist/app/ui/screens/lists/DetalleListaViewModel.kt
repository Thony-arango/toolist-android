package com.toolist.app.ui.screens.lists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.usecase.list.DeleteListUseCase
import com.toolist.app.domain.usecase.list.GetListUseCase
import com.toolist.app.domain.usecase.product.DeleteProductUseCase
import com.toolist.app.domain.usecase.product.DuplicateProductUseCase
import com.toolist.app.domain.usecase.product.MoveProductUseCase
import com.toolist.app.domain.usecase.product.ObserveProductsUseCase
import com.toolist.app.domain.usecase.product.ToggleProductStatusUseCase
import com.toolist.app.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UiState
// ---------------------------------------------------------------------------

data class DetalleListaUiState(
    val isLoading: Boolean = true,
    val list: ShoppingList? = null,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val tabs: List<String> = listOf("Todos"),
    val selectedTab: String = "Todos",
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class DetalleListaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getListUseCase: GetListUseCase,
    private val observeProductsUseCase: ObserveProductsUseCase,
    private val deleteListUseCase: DeleteListUseCase,
    private val toggleProductStatusUseCase: ToggleProductStatusUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val duplicateProductUseCase: DuplicateProductUseCase,
    private val moveProductUseCase: MoveProductUseCase,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val listId: String = checkNotNull(savedStateHandle["listId"])

    private val _uiState = MutableStateFlow(DetalleListaUiState())
    val uiState: StateFlow<DetalleListaUiState> = _uiState.asStateFlow()

    init {
        observeListAndProducts()
    }

    private fun observeListAndProducts() {
        viewModelScope.launch {
            combine(
                getListUseCase(listId),
                observeProductsUseCase(listId),
            ) { list, products ->
                val currentTab = _uiState.value.selectedTab
                val tabs = buildTabs(products)
                val validTab = if (tabs.contains(currentTab)) currentTab else "Todos"
                val filtered = filterProducts(products, validTab)
                DetalleListaUiState(
                    isLoading = false,
                    list = list,
                    products = products,
                    filteredProducts = filtered,
                    tabs = tabs,
                    selectedTab = validTab,
                    isCompleted = list?.isCompleted ?: false,
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.update { state.copy(isDeleted = it.isDeleted) } }
        }
    }

    fun selectTab(tab: String) {
        _uiState.update { state ->
            state.copy(
                selectedTab = tab,
                filteredProducts = filterProducts(state.products, tab),
            )
        }
    }

    fun toggleProductStatus(product: Product) {
        viewModelScope.launch {
            toggleProductStatusUseCase(product).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            deleteProductUseCase(product).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun duplicateProduct(product: Product) {
        viewModelScope.launch {
            duplicateProductUseCase(product).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun moveProduct(product: Product, targetListId: String) {
        viewModelScope.launch {
            moveProductUseCase(product, targetListId).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetList() {
        viewModelScope.launch {
            productRepository.resetAllProducts(listId).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteList() {
        viewModelScope.launch {
            deleteListUseCase(listId)
                .onSuccess { _uiState.update { it.copy(isDeleted = true) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun buildTabs(products: List<Product>): List<String> {
        val categories = products
            .mapNotNull { it.categoryName }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        return listOf("Todos") + categories
    }

    private fun filterProducts(products: List<Product>, tab: String): List<Product> =
        if (tab == "Todos") products else products.filter { it.categoryName == tab }
}

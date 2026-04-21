package com.toolist.app.ui.screens.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.usecase.list.GetListUseCase
import com.toolist.app.domain.usecase.product.ObserveProductsUseCase
import com.toolist.app.domain.usecase.product.ToggleProductStatusUseCase
import com.toolist.app.ui.navigation.Screen
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

data class DetalleProductoUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val listName: String = "",
    val error: String? = null,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class DetalleProductoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeProductsUseCase: ObserveProductsUseCase,
    private val getListUseCase: GetListUseCase,
    private val toggleProductStatusUseCase: ToggleProductStatusUseCase,
) : ViewModel() {

    val listId: String = checkNotNull(savedStateHandle[Screen.ProductDetail.ARG_LIST_ID])
    val productId: String = checkNotNull(savedStateHandle[Screen.ProductDetail.ARG_PRODUCT_ID])

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    init {
        observeProductAndList()
    }

    private fun observeProductAndList() {
        viewModelScope.launch {
            combine(
                observeProductsUseCase(listId),
                getListUseCase(listId),
            ) { products, list ->
                val product = products.firstOrNull { it.id == productId }
                DetalleProductoUiState(
                    isLoading = false,
                    product = product,
                    listName = list?.name ?: "",
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.value = state }
        }
    }

    fun toggleStatus() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            toggleProductStatusUseCase(product).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

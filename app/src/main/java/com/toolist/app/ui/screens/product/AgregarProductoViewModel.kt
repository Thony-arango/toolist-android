package com.toolist.app.ui.screens.product

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.usecase.list.GetListsUseCase
import com.toolist.app.domain.usecase.product.AddProductUseCase
import com.toolist.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// UiState
// ---------------------------------------------------------------------------

data class AgregarProductoUiState(
    val isLoading: Boolean = false,
    val lists: List<ShoppingList> = emptyList(),
    val nameError: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class AgregarProductoViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getListsUseCase: GetListsUseCase,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val listId: String = savedStateHandle[Screen.AddProduct.ARG_LIST_ID] ?: ""

    private val _uiState = MutableStateFlow(AgregarProductoUiState())
    val uiState: StateFlow<AgregarProductoUiState> = _uiState.asStateFlow()

    init {
        loadLists()
    }

    private fun loadLists() {
        viewModelScope.launch {
            getListsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { lists ->
                    _uiState.update { it.copy(lists = lists) }
                }
        }
    }

    fun addProduct(
        name: String,
        targetListId: String,
        quantity: String,
        unit: String,
        categoryName: String?,
        estimatedPrice: String,
        status: ProductStatus,
        notes: String,
    ) {
        val trimmedName = name.trim()
        val nameError = if (trimmedName.isEmpty()) {
            context.getString(R.string.add_product_error_name_required)
        } else null

        if (nameError != null) {
            _uiState.update { it.copy(nameError = nameError) }
            return
        }

        val parsedQuantity = quantity.toDoubleOrNull() ?: 1.0
        val parsedPrice = estimatedPrice.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, nameError = null, error = null) }
            val product = Product(
                id = "",
                listId = targetListId,
                name = trimmedName,
                quantity = parsedQuantity,
                unit = unit,
                categoryId = null,
                categoryName = categoryName,
                estimatedPrice = parsedPrice,
                status = status,
                notes = notes.trim().ifEmpty { null },
                imageUrl = null,
            )
            addProductUseCase(product)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun clearNameError() {
        _uiState.update { it.copy(nameError = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

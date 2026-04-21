package com.toolist.app.ui.screens.lists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toolist.app.R
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.usecase.list.CreateListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UiState

data class NuevaListaUiState(
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

// ViewModel

@HiltViewModel
class NuevaListaViewModel @Inject constructor(
    private val createListUseCase: CreateListUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaListaUiState())
    val uiState: StateFlow<NuevaListaUiState> = _uiState.asStateFlow()

    fun createList(name: String, colorHex: String, description: String) {
        val trimmedName = name.trim()
        val nameError = when {
            trimmedName.isEmpty() -> context.getString(R.string.new_list_error_name_required)
            trimmedName.length > 50 -> context.getString(R.string.new_list_error_name_too_long)
            else -> null
        }
        if (nameError != null) {
            _uiState.update { it.copy(nameError = nameError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { NuevaListaUiState(isLoading = true) }
            val list = ShoppingList(
                id = "",
                name = trimmedName,
                description = description.trim(),
                colorHex = colorHex,
                totalEstimated = 0.0,
                purchasedCount = 0,
                totalCount = 0,
                createdAt = System.currentTimeMillis(),
            )
            createListUseCase(list)
                .onSuccess { _uiState.update { NuevaListaUiState(isSuccess = true) } }
                .onFailure { e -> _uiState.update { NuevaListaUiState(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

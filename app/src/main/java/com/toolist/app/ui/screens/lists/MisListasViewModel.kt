package com.toolist.app.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.usecase.list.CreateListUseCase
import com.toolist.app.domain.usecase.list.DeleteListUseCase
import com.toolist.app.domain.usecase.list.GetListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

data class MisListasUiState(
    val isLoading: Boolean = true,
    val lists: List<ShoppingList> = emptyList(),
    val totalEstimated: Double = 0.0,
    val totalProducts: Int = 0,
    val error: String? = null,
    val userName: String = "",
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class MisListasViewModel @Inject constructor(
    private val getListsUseCase: GetListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val deleteListUseCase: DeleteListUseCase,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MisListasUiState(userName = auth.currentUser?.displayName?.split(" ")?.firstOrNull() ?: ""),
    )
    val uiState: StateFlow<MisListasUiState> = _uiState.asStateFlow()

    init {
        observeLists()
    }

    private fun observeLists() {
        viewModelScope.launch {
            getListsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { lists ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            lists = lists,
                            totalEstimated = lists.sumOf { list -> list.totalEstimated },
                            totalProducts = lists.sumOf { list -> list.totalCount },
                            error = null,
                        )
                    }
                }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            deleteListUseCase(listId).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun duplicateList(list: ShoppingList) {
        viewModelScope.launch {
            val copy = list.copy(
                id = "",
                name = "Copia de ${list.name}",
                purchasedCount = 0,
                totalCount = 0,
                totalEstimated = 0.0,
                createdAt = System.currentTimeMillis(),
            )
            createListUseCase(copy).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

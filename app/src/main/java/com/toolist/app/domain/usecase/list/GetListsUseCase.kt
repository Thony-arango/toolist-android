package com.toolist.app.domain.usecase.list

import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListsUseCase @Inject constructor(
    private val repository: ListRepository,
) {
    operator fun invoke(): Flow<List<ShoppingList>> = repository.observeLists()
}

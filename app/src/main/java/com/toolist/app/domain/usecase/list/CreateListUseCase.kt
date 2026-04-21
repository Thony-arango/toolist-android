package com.toolist.app.domain.usecase.list

import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.domain.repository.ListRepository
import javax.inject.Inject

class CreateListUseCase @Inject constructor(
    private val repository: ListRepository,
) {
    suspend operator fun invoke(list: ShoppingList): Result<String> =
        repository.createList(list)
}

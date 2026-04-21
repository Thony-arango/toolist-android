package com.toolist.app.domain.usecase.list

import com.toolist.app.domain.repository.ListRepository
import javax.inject.Inject

class DeleteListUseCase @Inject constructor(
    private val repository: ListRepository,
) {
    suspend operator fun invoke(listId: String): Result<Unit> =
        repository.deleteList(listId)
}

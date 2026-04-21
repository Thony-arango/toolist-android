package com.toolist.app.domain.usecase.product

import com.toolist.app.domain.model.Product
import com.toolist.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductsUseCase @Inject constructor(private val repository: ProductRepository) {
    operator fun invoke(listId: String): Flow<List<Product>> = repository.observeProducts(listId)
}

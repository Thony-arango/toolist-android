package com.toolist.app.domain.usecase.product

import com.toolist.app.domain.model.Product
import com.toolist.app.domain.repository.ProductRepository
import javax.inject.Inject

class ToggleProductStatusUseCase @Inject constructor(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product): Result<Unit> = repository.toggleProductStatus(product)
}

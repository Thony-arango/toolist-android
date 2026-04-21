package com.toolist.app.di

import com.toolist.app.data.repository.ListRepositoryImpl
import com.toolist.app.domain.repository.ListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindListRepository(impl: ListRepositoryImpl): ListRepository
}

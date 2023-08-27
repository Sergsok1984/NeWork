package ru.sokolov_diplom.nework.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sokolov_diplom.nework.repository.EventRepository
import ru.sokolov_diplom.nework.repository.EventRepositoryImpl
import ru.sokolov_diplom.nework.repository.PostRepository
import ru.sokolov_diplom.nework.repository.PostRepositoryImpl
import ru.sokolov_diplom.nework.repository.ProfileRepository
import ru.sokolov_diplom.nework.repository.ProfileRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsEventRepository(impl: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}

package ru.sokolov_diplom.nework.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sokolov_diplom.nework.repository.user.UserRepository
import ru.sokolov_diplom.nework.repository.user.UserRepositoryImpl
import ru.sokolov_diplom.nework.repository.event.EventRepository
import ru.sokolov_diplom.nework.repository.event.EventRepositoryImpl
import ru.sokolov_diplom.nework.repository.post.PostRepository
import ru.sokolov_diplom.nework.repository.post.PostRepositoryImpl
import ru.sokolov_diplom.nework.repository.profile.ProfileRepository
import ru.sokolov_diplom.nework.repository.profile.ProfileRepositoryImpl
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

    @Singleton
    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}

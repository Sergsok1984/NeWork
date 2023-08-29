package ru.sokolov_diplom.nework.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sokolov_diplom.nework.dao.event.EventDao
import ru.sokolov_diplom.nework.dao.event.EventRemoteKeyDao
import ru.sokolov_diplom.nework.dao.job.JobDao
import ru.sokolov_diplom.nework.dao.post.PostDao
import ru.sokolov_diplom.nework.dao.post.PostRemoteKeyDao
import ru.sokolov_diplom.nework.dao.user.UserDao
import ru.sokolov_diplom.nework.dao.wall.WallPostDao
import ru.sokolov_diplom.nework.dao.wall.WallRemoteKeyDao
import ru.sokolov_diplom.nework.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideEventDao(db: AppDb): EventDao = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()

    @Provides
    fun provideJobDao(db: AppDb): JobDao = db.jobDao()

    @Provides
    fun provideWallDao(db: AppDb): WallPostDao = db.wallPostDao()

    @Provides
    fun provideWallRemoteKeyDao(db: AppDb): WallRemoteKeyDao = db.wallRemoteKeyDao()

    @Provides
    fun provideUserDao(db: AppDb): UserDao = db.userDao()
}

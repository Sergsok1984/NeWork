package ru.sokolov_diplom.nework.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolov_diplom.nework.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): PostsDb = Room.databaseBuilder(context, PostsDb::class.java, "Posts.db")
        .build()

    @Provides
    fun providePostDao(
        postsDb: PostsDb
    ): PostDao = postsDb.postDao()
}

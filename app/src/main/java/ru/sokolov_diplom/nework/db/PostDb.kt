package ru.sokolov_diplom.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class PostsDb : RoomDatabase() {

    abstract fun postDao(): PostDao
}

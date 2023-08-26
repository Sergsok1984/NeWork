package ru.sokolov_diplom.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sokolov_diplom.nework.dao.EventDao
import ru.sokolov_diplom.nework.dao.EventRemoteKeyDao
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.dao.PostRemoteKeyDao
import ru.sokolov_diplom.nework.dao.UserDao
import ru.sokolov_diplom.nework.entity.EventEntity
import ru.sokolov_diplom.nework.entity.EventRemoteKeyEntity
import ru.sokolov_diplom.nework.entity.UserEntity
import ru.sokolov_diplom.nework.entity.PostEntity
import ru.sokolov_diplom.nework.entity.PostRemoteKeyEntity
import ru.sokolov_diplom.nework.util.Converters

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class,
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userDao(): UserDao
}

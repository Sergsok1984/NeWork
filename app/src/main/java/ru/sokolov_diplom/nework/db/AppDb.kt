package ru.sokolov_diplom.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sokolov_diplom.nework.dao.WallPostDao
import ru.sokolov_diplom.nework.dao.EventDao
import ru.sokolov_diplom.nework.dao.EventRemoteKeyDao
import ru.sokolov_diplom.nework.dao.JobDao
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.dao.PostRemoteKeyDao
import ru.sokolov_diplom.nework.dao.WallRemoteKeyDao
import ru.sokolov_diplom.nework.entity.EventEntity
import ru.sokolov_diplom.nework.entity.EventRemoteKeyEntity
import ru.sokolov_diplom.nework.entity.JobEntity
import ru.sokolov_diplom.nework.entity.PostEntity
import ru.sokolov_diplom.nework.entity.PostRemoteKeyEntity
import ru.sokolov_diplom.nework.entity.WallPostEntity
import ru.sokolov_diplom.nework.entity.WallRemoteKeyEntity
import ru.sokolov_diplom.nework.util.Converters

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        JobEntity::class,
        WallPostEntity::class,
        WallRemoteKeyEntity::class,
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun jobDao(): JobDao
    abstract fun wallPostDao(): WallPostDao
    abstract fun wallRemoteKeyDao(): WallRemoteKeyDao
}

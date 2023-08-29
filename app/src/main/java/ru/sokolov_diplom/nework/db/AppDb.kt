package ru.sokolov_diplom.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sokolov_diplom.nework.dao.wall.WallPostDao
import ru.sokolov_diplom.nework.dao.event.EventDao
import ru.sokolov_diplom.nework.dao.event.EventRemoteKeyDao
import ru.sokolov_diplom.nework.dao.job.JobDao
import ru.sokolov_diplom.nework.dao.post.PostDao
import ru.sokolov_diplom.nework.dao.post.PostRemoteKeyDao
import ru.sokolov_diplom.nework.dao.user.UserDao
import ru.sokolov_diplom.nework.dao.wall.WallRemoteKeyDao
import ru.sokolov_diplom.nework.entity.user.UserEntity
import ru.sokolov_diplom.nework.entity.event.EventEntity
import ru.sokolov_diplom.nework.entity.event.EventRemoteKeyEntity
import ru.sokolov_diplom.nework.entity.job.JobEntity
import ru.sokolov_diplom.nework.entity.post.PostEntity
import ru.sokolov_diplom.nework.entity.post.PostRemoteKeyEntity
import ru.sokolov_diplom.nework.entity.wall.WallPostEntity
import ru.sokolov_diplom.nework.entity.wall.WallRemoteKeyEntity
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
        UserEntity::class,
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
    abstract fun userDao(): UserDao
}

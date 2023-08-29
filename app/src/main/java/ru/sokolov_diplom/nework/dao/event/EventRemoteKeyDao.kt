package ru.sokolov_diplom.nework.dao.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.sokolov_diplom.nework.entity.event.EventRemoteKeyEntity

@Dao
interface EventRemoteKeyDao {

    @Query("SELECT COUNT(*) == 0 FROM EventRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(`key`) FROM EventRemoteKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT MIN(`key`) FROM EventRemoteKeyEntity")
    suspend fun min(): Int?

    @Query("DELETE FROM EventRemoteKeyEntity")
    suspend fun removeAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: EventRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: List<EventRemoteKeyEntity>)
}

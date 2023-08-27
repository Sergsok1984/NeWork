package ru.sokolov_diplom.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.sokolov_diplom.nework.entity.WallRemoteKeyEntity

@Dao
interface WallRemoteKeyDao {

    @Query("SELECT MAX(id) FROM WallRemoteKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT MIN(id) FROM WallRemoteKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(wallRemoteKeyEntity: WallRemoteKeyEntity)

    @Query("DELETE FROM WallRemoteKeyEntity")
    suspend fun clear()
}

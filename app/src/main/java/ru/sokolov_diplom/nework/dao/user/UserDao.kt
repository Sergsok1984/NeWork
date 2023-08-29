package ru.sokolov_diplom.nework.dao.user

import androidx.room.*
import androidx.room.OnConflictStrategy
import ru.sokolov_diplom.nework.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity ORDER BY name ASC")
    fun getAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Query("DELETE FROM UserEntity WHERE id = :id")
    suspend fun removeById(id: Int)
}

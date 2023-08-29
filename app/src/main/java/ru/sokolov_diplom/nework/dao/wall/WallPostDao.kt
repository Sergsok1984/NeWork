package ru.sokolov_diplom.nework.dao.wall

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.sokolov_diplom.nework.entity.wall.WallPostEntity

@Dao
interface WallPostDao {

    @Query("SELECT * FROM WallPostEntity ORDER BY id DESC")
    fun getWallPagingSource(): PagingSource<Int, WallPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<WallPostEntity>)

    @Query("DELETE FROM WallPostEntity")
    suspend fun removeAllPosts()
}

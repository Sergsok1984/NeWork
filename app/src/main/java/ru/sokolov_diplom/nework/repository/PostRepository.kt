package ru.sokolov_diplom.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun getAllPosts()
    suspend fun savePost(post: Post)
    suspend fun removePostById(id: Int)
}
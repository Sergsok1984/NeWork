package ru.sokolov_diplom.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.dto.AttachmentType
import ru.sokolov_diplom.nework.dto.MediaUpload
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.dto.Media

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun savePost(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload, type: AttachmentType)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun removePostById(id: Int)
    suspend fun likePostById(id: Int)
    suspend fun unlikePostById(id: Int)
}

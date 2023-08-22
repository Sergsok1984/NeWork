package ru.sokolov_diplom.nework.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import ru.sokolov_diplom.nework.api.Api
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.toEntity
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: Api
) : PostRepository {

    override val flow = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(
                apiService
            )
        }
    ).flow

    override suspend fun getAll() {
        val body: List<Post>
        try {
            val response = apiService.getAllPosts()
            if (!response.isSuccessful) {
                throw Exception("Response was not successful")
            }
            body = response.body()!!
            dao.insert(body.toEntity())
        } catch (e: Exception) {
            throw e
        }
    }
}

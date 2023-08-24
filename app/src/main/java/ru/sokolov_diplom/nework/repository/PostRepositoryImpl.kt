package ru.sokolov_diplom.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.map
import ru.sokolov_diplom.nework.api.PostsApiService
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.dao.PostRemoteKeyDao
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.PostEntity
import ru.sokolov_diplom.nework.entity.toEntity
import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.db.AppDb
import ru.sokolov_diplom.nework.error.ApiException
import ru.sokolov_diplom.nework.error.NetworkException
import ru.sokolov_diplom.nework.error.UnknownException
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postsApiService: PostsApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(postsApiService, postDao, postRemoteKeyDao, appDb)
    ).flow
        .map { it.map(PostEntity::toDto) }

    override suspend fun getAllPosts() {
        val body: List<Post>
        try {
            val response = postsApiService.getAllPosts()
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            body = response.body() ?: throw ApiException(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            val response = postsApiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun removePostById(id: Int) {
        try {
            val response = postsApiService.removePostById(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            postDao.removePostById(id)
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun likePostById(id: Int) {
        try {
            postDao.likeById(id)
            val response = postsApiService.likePostById(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun unlikePostById(id: Int) {
        try {
            postDao.unlikeById(id)
            val response = postsApiService.unlikePostById(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}

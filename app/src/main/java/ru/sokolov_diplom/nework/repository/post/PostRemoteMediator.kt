package ru.sokolov_diplom.nework.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.sokolov_diplom.nework.api.PostsApiService
import ru.sokolov_diplom.nework.dao.PostDao
import ru.sokolov_diplom.nework.dao.PostRemoteKeyDao
import retrofit2.HttpException
import ru.sokolov_diplom.nework.db.AppDb
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.post.PostEntity
import ru.sokolov_diplom.nework.entity.post.PostRemoteKeyEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postsApiService: PostsApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    postsApiService.getLatestPosts(state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    postsApiService.getPostsAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postsApiService.getPostsBefore(id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val body = result.body() ?: throw Error(result.message())

            if (body.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postDao.removeAll()
                        insertMaxKey(body)
                        insertMinKey(body)
                        postDao.removeAll()
                    }

                    LoadType.APPEND -> insertMinKey(body)
                    LoadType.PREPEND -> insertMaxKey(body)
                }

                postDao.insert(body.map(PostEntity.Companion::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun insertMaxKey(body: List<Post>) {
        postRemoteKeyDao.insert(
            PostRemoteKeyEntity(
                PostRemoteKeyEntity.KeyType.AFTER,
                body.first().id
            )
        )
    }

    private suspend fun insertMinKey(body: List<Post>) {
        postRemoteKeyDao.insert(
            PostRemoteKeyEntity(
                PostRemoteKeyEntity.KeyType.BEFORE,
                body.last().id,
            )
        )
    }
}

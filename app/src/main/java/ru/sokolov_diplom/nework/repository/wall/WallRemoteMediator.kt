package ru.sokolov_diplom.nework.repository.wall

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.sokolov_diplom.nework.dao.wall.WallPostDao
import ru.sokolov_diplom.nework.entity.wall.WallRemoteKeyEntity
import ru.sokolov_diplom.nework.api.PostsApiService
import ru.sokolov_diplom.nework.dao.wall.WallRemoteKeyDao
import ru.sokolov_diplom.nework.db.AppDb
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.wall.WallPostEntity
import ru.sokolov_diplom.nework.error.ApiException

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val postsApiService: PostsApiService,
    private val wallPostDao: WallPostDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,
    private val authorId: Int,
) : RemoteMediator<Int, WallPostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallPostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    postsApiService.getLatestWallPosts(authorId, state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    val maxKey = wallRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    postsApiService.getWallPostsAfter(maxKey, authorId, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val minKey = wallRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    postsApiService.getWallPostsBefore(minKey, authorId, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) throw ApiException(response.code(), response.message())
            val body = response.body() ?: throw Error(response.message())

            if (body.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        wallRemoteKeyDao.clear()
                        insertMaxKey(body)
                        insertMinKey(body)
                        wallPostDao.removeAllPosts()
                    }

                    LoadType.PREPEND -> insertMaxKey(body)
                    LoadType.APPEND -> insertMinKey(body)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }


    private suspend fun insertMaxKey(body: List<Post>) {
        wallRemoteKeyDao.insertKey(
            WallRemoteKeyEntity(
                WallRemoteKeyEntity.KeyType.AFTER,
                body.first().id
            )
        )
    }

    private suspend fun insertMinKey(body: List<Post>) {
        wallRemoteKeyDao.insertKey(
            WallRemoteKeyEntity(
                WallRemoteKeyEntity.KeyType.BEFORE,
                body.last().id,
            )
        )
    }
}

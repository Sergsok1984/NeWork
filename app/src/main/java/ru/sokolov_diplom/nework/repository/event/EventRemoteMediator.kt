package ru.sokolov_diplom.nework.repository.event

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.sokolov_diplom.nework.api.EventApiService
import ru.sokolov_diplom.nework.dao.event.EventDao
import ru.sokolov_diplom.nework.dao.event.EventRemoteKeyDao
import ru.sokolov_diplom.nework.db.AppDb
import ru.sokolov_diplom.nework.dto.Event
import retrofit2.HttpException
import ru.sokolov_diplom.nework.entity.event.EventEntity
import ru.sokolov_diplom.nework.entity.event.EventRemoteKeyEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>,
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    eventApiService.getEventLatest(state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    eventApiService.getEventAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    eventApiService.getEventBefore(id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) throw HttpException(result)
            val body = result.body() ?: throw Error(result.message())

            if (body.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventDao.removeAll()
                        insertMaxKey(body)
                        insertMinKey(body)
                        eventDao.removeAll()
                    }

                    LoadType.APPEND -> insertMinKey(body)
                    LoadType.PREPEND -> insertMaxKey(body)
                }
                eventDao.insertEvents(body.map(EventEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun insertMaxKey(body: List<Event>) {
        eventRemoteKeyDao.insert(
            EventRemoteKeyEntity(
                EventRemoteKeyEntity.KeyType.AFTER,
                body.first().id
            )
        )
    }

    private suspend fun insertMinKey(body: List<Event>) {
        eventRemoteKeyDao.insert(
            EventRemoteKeyEntity(
                EventRemoteKeyEntity.KeyType.BEFORE,
                body.last().id,
            )
        )
    }
}

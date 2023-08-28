package ru.sokolov_diplom.nework.repository.event

import androidx.paging.PagingData
import ru.sokolov_diplom.nework.dto.Event
import ru.sokolov_diplom.nework.dto.Media
import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.dto.MediaUpload

interface EventRepository {

    val data: Flow<PagingData<Event>>

    suspend fun saveEvent(event: Event)

    suspend fun saveWithAttachment(event: Event, upload: MediaUpload)

    suspend fun uploadWithContent(upload: MediaUpload): Media

    suspend fun removeById(id: Int)

    suspend fun likeById(id: Int)

    suspend fun unlikeById(id: Int)

    suspend fun participate(id: Int)

    suspend fun doNotParticipate(id: Int)
}

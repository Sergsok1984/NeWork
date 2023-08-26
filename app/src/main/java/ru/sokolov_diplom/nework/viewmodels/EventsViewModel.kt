package ru.sokolov_diplom.nework.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import ru.sokolov_diplom.nework.auth.AppAuth
import ru.sokolov_diplom.nework.dto.AttachmentType
import ru.sokolov_diplom.nework.dto.Event
import ru.sokolov_diplom.nework.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.dto.EventType
import ru.sokolov_diplom.nework.dto.MediaModel
import ru.sokolov_diplom.nework.dto.MediaUpload
import ru.sokolov_diplom.nework.model.StateModel
import ru.sokolov_diplom.nework.util.SingleLiveEvent
import java.io.InputStream
import javax.inject.Inject


val emptyEvent = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    datetime = "",
    type = EventType.ONLINE,
    speakerIds = emptySet()
)

private val noMedia = MediaModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val cached = eventRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> =
        appAuth.state
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { event ->
                        event.copy(
                            ownedByMe = event.authorId == myId,
                            likedByMe = event.likeOwnerIds.contains(myId),
                            participatedByMe = event.participantsIds.contains(myId)
                        )
                    }
                }
            }

    val edited = MutableLiveData(emptyEvent)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable>
        get() = _error

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    private val scope = MainScope()

    fun save() {
        edited.value?.let { event ->
            viewModelScope.launch {
                _dataState.postValue(StateModel(loading = true))
                try {
                    when (_media.value) {
                        noMedia ->
                            eventRepository.saveEvent(event)

                        else ->
                            _media.value?.inputStream?.let {
                                MediaUpload(it)
                            }?.let {
                                eventRepository.saveWithAttachment(event, it)
                            }
                    }
                    _dataState.value = StateModel()
                    _eventCreated.value = Unit
                } catch (e: Exception) {
                    throw UnknownError()
                }
            }
        }
        edited.value = emptyEvent
        _media.value = noMedia
    }

    fun changeContent(content: String, date: String) {
        edited.value?.let {
            val text = content.trim()

            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
            if (edited.value?.datetime != date) {
                edited.value = edited.value?.copy(datetime = date)
            }
        }
    }

    fun changeEventType(isOnlineChecked: Boolean) {
        edited.value?.let {
            if (isOnlineChecked && edited.value?.type != EventType.ONLINE) {
                edited.value = edited.value?.copy(type = EventType.ONLINE)
            }
            if (!isOnlineChecked && edited.value?.type != EventType.OFFLINE) {
                edited.value = edited.value?.copy(type = EventType.OFFLINE)
            }
        }
    }

    fun changeMedia(
        uri: Uri?,
        inputStream: InputStream?,
        type: AttachmentType?,
    ) {
        _media.value = MediaModel(uri, inputStream, type)
    }

    fun removeMedia() {
        _media.value = noMedia
    }

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            eventRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun likeById(id: Int) = viewModelScope.launch {
        try {
            eventRepository.likeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun unlikeById(id: Int) = viewModelScope.launch {
        try {
            eventRepository.unlikeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun participate(id: Int) = viewModelScope.launch {
        try {
            eventRepository.participate(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun doNotParticipate(id: Int) = viewModelScope.launch {
        try {
            eventRepository.doNotParticipate(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}

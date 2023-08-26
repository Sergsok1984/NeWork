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
import ru.sokolov_diplom.nework.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.dto.AttachmentType
import ru.sokolov_diplom.nework.dto.MediaModel
import ru.sokolov_diplom.nework.dto.MediaUpload
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.model.StateModel
import ru.sokolov_diplom.nework.util.SingleLiveEvent
import java.io.InputStream
import javax.inject.Inject

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    authorId = 0,
    authorJob = null,
    content = "",
    likedByMe = false,
    link = null,
    mentionIds = emptySet(),
    mentionedMe = false,
    likeOwnerIds = emptySet(),
    ownedByMe = false,
    published = "",
    attachment = null
)

private val noMedia = MediaModel(null, null)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> =
        appAuth.state
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { post ->
                        post.copy(
                            ownedByMe = post.authorId == myId,
                            likedByMe = post.likeOwnerIds.contains(myId)
                        )
                    }
                }
            }
    private val _dataState = MutableLiveData(StateModel())

    val dataState: LiveData<StateModel>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable>
        get() = _error

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            _dataState.value = StateModel(refreshing = true)
            repository.removePostById(id)
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun likeById(id: Int) = viewModelScope.launch {
        try {
            repository.likePostById(id)
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun unlikeById(id: Int) = viewModelScope.launch {
        try {
            repository.unlikePostById(id)
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun save() {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_media.value) {
                        noMedia -> repository.savePost(post)
                        else -> _media.value?.inputStream?.let {
                            MediaUpload(it)
                        }?.let {
                            repository.saveWithAttachment(post, it, _media.value?.type!!)
                        }
                    }
                    _dataState.value = StateModel()
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        edited.value = empty
        _media.value = noMedia
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _media.value = MediaModel(uri, inputStream, type)
    }

    fun deleteMedia() {
        _media.value = noMedia
    }
}

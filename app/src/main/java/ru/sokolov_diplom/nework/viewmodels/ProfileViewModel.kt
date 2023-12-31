package ru.sokolov_diplom.nework.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import ru.sokolov_diplom.nework.authorization.AppAuth
import ru.sokolov_diplom.nework.dto.Job
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.model.StateModel
import ru.sokolov_diplom.nework.repository.profile.ProfileRepositoryImpl
import javax.inject.Inject

val emptyJob = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: ProfileRepositoryImpl
) : ViewModel() {

    val editedJob = MutableLiveData(Job())

    private val _dataState = MutableLiveData(StateModel())
    val dataState: LiveData<StateModel>
        get() = _dataState

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    fun getWallPosts(userId: Int): Flow<PagingData<Post>> = appAuth.state
        .flatMapLatest { (myId, _) ->
            repository.getWallPosts(userId)
                .map { postList ->
                    postList.map {
                        it.copy(
                            ownedByMe = it.authorId == myId
                        )
                    }
                }
        }
        .cachedIn(viewModelScope)

    fun getLatestWallPosts(userId: Int) {
        viewModelScope.launch {
            try {
                _dataState.value = StateModel(loading = true)
                repository.getLatestWallPosts(userId)
                _dataState.value = StateModel(loading = false)
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun loadJobs(authorId: Int) = viewModelScope.launch {
        try {
            _dataState.value = StateModel(loading = true)
            repository.loadJobs(authorId)
            _dataState.value = StateModel(loading = false)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getAllJobs(): LiveData<List<Job>> = repository.getAllJobs()

    fun saveJob() {
        editedJob.value?.let { job ->
            viewModelScope.launch {
                try {
                    _dataState.value = StateModel(loading = true)
                    repository.saveJob(job)
                    _dataState.value = StateModel(loading = false)
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }

            }
        }
        clearEditedJob()
    }

    fun edit(job: Job) {
        editedJob.value = job
    }

    fun changeJobContent(
        name: String,
        position: String,
        start: String,
        finish: String,
        link: String
    ) {
        if (editedJob.value?.name != name) {
            editedJob.value = editedJob.value?.copy(name = name)
        }
        if (editedJob.value?.position != position) {
            editedJob.value = editedJob.value?.copy(position = position)
        }
        if (editedJob.value?.start != start) {
            editedJob.value = editedJob.value?.copy(start = start)
        }
        if (editedJob.value?.finish != finish) {
            editedJob.value = editedJob.value?.copy(finish = finish)
        }
        if (editedJob.value?.link != link) {
            editedJob.value = editedJob.value?.copy(link = link)
        }
    }

    fun deleteJobById(id: Int) {
        viewModelScope.launch {
            try {
                _dataState.value = StateModel(loading = true)
                repository.deleteJobById(id)
                _dataState.value = StateModel(loading = false)
            } catch (e: Exception) {
                _dataState.value = StateModel(error = true)
            }
        }
    }

    fun clearEditedJob() {
        editedJob.value = emptyJob
    }
}

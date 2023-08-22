package ru.sokolov_diplom.nework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repository: PostRepository,
) : ViewModel() {

    val flow: Flow<PagingData<Post>> = repository.flow
        .flowOn(Dispatchers.Default)

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            repository.getAll()
        } catch (e: Exception) {
            throw e
        }
    }
}

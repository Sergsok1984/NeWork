package ru.sokolov_diplom.nework.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ru.sokolov_diplom.nework.dto.Post
import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.dto.Job

interface ProfileRepository {
    fun getWallPosts(authorId: Int): Flow<PagingData<Post>>
    fun getAllJobs(): LiveData<List<Job>>
    suspend fun loadJobs(authorId: Int)
    suspend fun saveJob(job: Job)
    suspend fun deleteJobById(id: Int)
}

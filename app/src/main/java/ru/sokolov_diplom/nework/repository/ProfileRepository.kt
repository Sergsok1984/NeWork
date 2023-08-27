package ru.sokolov_diplom.nework.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.*
import ru.sokolov_diplom.nework.dao.WallPostDao
import ru.sokolov_diplom.nework.api.JobsApiService
import ru.sokolov_diplom.nework.api.PostsApiService
import ru.sokolov_diplom.nework.dao.JobDao
import ru.sokolov_diplom.nework.dao.WallRemoteKeyDao
import ru.sokolov_diplom.nework.db.AppDb
import ru.sokolov_diplom.nework.dto.Job
import ru.sokolov_diplom.nework.error.DbException
import ru.sokolov_diplom.nework.error.NetworkException
import ru.sokolov_diplom.nework.error.UnknownException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.JobEntity
import ru.sokolov_diplom.nework.entity.fromDto
import ru.sokolov_diplom.nework.error.ApiException
import java.io.IOException
import java.sql.SQLException
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val postsApiService: PostsApiService,
    private val jobsApiService: JobsApiService,
    private val appDb: AppDb,
    private val wallPostDao: WallPostDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val jobDao: JobDao,
) {

    @ExperimentalPagingApi
    fun getWallPosts(authorId: Int): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_WALL_PAGE_SIZE, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            postsApiService,
            wallPostDao,
            wallRemoteKeyDao,
            appDb,
            authorId
        ),
        pagingSourceFactory = { wallPostDao.getWallPagingSource() }
    ).flow.map { postList ->
        postList.map { it.toDto() }
    }

    fun getAllJobs(): LiveData<List<Job>> = jobDao.getAllJobs().map { jobList ->
        jobList.map { it.toDto() }
    }

    suspend fun loadJobs(authorId: Int) {
        try {
            jobDao.removeAllJobs()
            val response = jobsApiService.getJobsById(authorId)
            if (!response.isSuccessful) throw ApiException(response.code(), response.message())
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            jobDao.insertJobs(body.fromDto())
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: SQLException) {
            throw DbException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    suspend fun saveJob(job: Job) {
        try {
            val response = jobsApiService.saveMyJob(job)
            if (!response.isSuccessful) throw ApiException(response.code(), response.message())
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            jobDao.insertJob(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: SQLException) {
            throw DbException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    suspend fun deleteJobById(id: Int) {
        val jobToDelete = jobDao.getJobById(id)
        try {
            jobDao.removeJobById(id)

            val response = jobsApiService.removeMyJobById(id)
            if (!response.isSuccessful) {
                jobDao.insertJob(jobToDelete)
                throw ApiException(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: SQLException) {
            throw DbException
        } catch (e: Exception) {
            throw UnknownException
        }
    }
}

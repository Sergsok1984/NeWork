package ru.sokolov_diplom.nework.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.sokolov_diplom.nework.api.Api
import retrofit2.HttpException
import ru.sokolov_diplom.nework.dto.Post
import java.io.IOException

class PostPagingSource(
    private val apiService: Api
) : PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatestPosts(params.loadSize)
                }

                is LoadParams.Append -> {
                    apiService.getPostsBefore(id = params.key, count = params.loadSize)
                }

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            if (!result.isSuccessful) {
                throw HttpException(result)
            }

            val data = result.body().orEmpty()
            return LoadResult.Page(data = data, prevKey = params.key, data.lastOrNull()?.id)
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}

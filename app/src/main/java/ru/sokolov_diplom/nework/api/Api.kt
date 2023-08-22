package ru.sokolov_diplom.nework.api


import ru.sokolov_diplom.nework.dto.Event
import ru.sokolov_diplom.nework.dto.Job
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.sokolov_diplom.nework.dto.Post

interface Api {

    @GET("/api/posts/")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("/api/posts/latest/")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("/api/posts/{post_id}/newer")
    suspend fun getNewerPosts(@Path("post_id") id: Int): Response<List<Post>>

    @GET("/api/posts/{post_id}/before")
    suspend fun getPostsBefore(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/posts/{post_id}/after")
    suspend fun getPostsAfter(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("/api/{authorId}/wall/")
    suspend fun getWallById(@Path("authorId") authorId: Int): Response<List<Post>>

    @GET("/api/{userId}/jobs/")
    suspend fun getJobsById(@Path("userId") userId: Int): Response<List<Job>>

    @GET("/api/events/")
    suspend fun getAllEvents(): Response<List<Event>>
}

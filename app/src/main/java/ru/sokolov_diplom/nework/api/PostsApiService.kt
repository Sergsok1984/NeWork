package ru.sokolov_diplom.nework.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.sokolov_diplom.nework.dto.Post

interface PostsApiService {

    @GET("/api/posts/")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("/api/posts/latest/")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

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

    @DELETE("/api/posts/{post_id}")
    suspend fun removePostById(@Path("post_id") id: Int): Response<Unit>

    @POST("/api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @POST("/api/posts/{post_id}/likes")
    suspend fun likePostById(@Path("post_id") id: Int): Response<Post>

    @DELETE("/api/posts/{post_id}/likes")
    suspend fun unlikePostById(@Path("post_id") id: Int): Response<Post>
}
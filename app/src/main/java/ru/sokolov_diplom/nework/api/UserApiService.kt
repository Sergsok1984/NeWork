package ru.sokolov_diplom.nework.api

import ru.sokolov_diplom.nework.authorization.AuthState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.sokolov_diplom.nework.dto.User

interface UserApiService {

    @FormUrlEncoded
    @POST("/api/users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("password") password: String,
    ): Response<AuthState>

    @Multipart
    @POST("/api/users/registration")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?,
    ): Response<AuthState>

    @GET("/api/users")
    suspend fun getUsers(): Response<List<User>>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>
}

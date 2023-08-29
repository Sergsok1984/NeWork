package ru.sokolov_diplom.nework.repository.user

import ru.sokolov_diplom.nework.api.UserApiService
import ru.sokolov_diplom.nework.dao.user.UserDao
import ru.sokolov_diplom.nework.entity.user.toDto
import ru.sokolov_diplom.nework.entity.user.toUserEntity
import ru.sokolov_diplom.nework.error.NetworkException
import ru.sokolov_diplom.nework.error.UnknownException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.sokolov_diplom.nework.dto.User
import ru.sokolov_diplom.nework.error.ApiException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApiService: UserApiService,
) : UserRepository {

    override val data: Flow<List<User>> =
        userDao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            userDao.getAll()
            val response = userApiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            userDao.insert(body.toUserEntity())
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }
}

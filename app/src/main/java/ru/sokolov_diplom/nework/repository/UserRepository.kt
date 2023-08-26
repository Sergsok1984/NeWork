package ru.sokolov_diplom.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolov_diplom.nework.dto.User

interface UserRepository {

    val data: Flow<List<User>>

    suspend fun getAll()
}

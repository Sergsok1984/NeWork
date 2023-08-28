package ru.sokolov_diplom.nework.error

import java.io.IOException
import java.sql.SQLException

sealed class AppError(val code: Int, info: String) : RuntimeException(info) {
    companion object {
        fun from(e: Throwable) = when (e) {
            is IOException -> NetworkException
            is SQLException -> DbException
            is ApiException -> e
            else -> UnknownException
        }
    }
}

class ApiException(code: Int, message: String) : AppError(code, message)
data object DbException : AppError(-1, "error_db")
data object NetworkException : AppError(-1, "no_network")
data object UnknownException : AppError(-1, "unknown")

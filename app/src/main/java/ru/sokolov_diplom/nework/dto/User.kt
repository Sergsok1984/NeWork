package ru.sokolov_diplom.nework.dto

data class User(
    val id: Int,
    val login: String?,
    val name: String,
    val avatar: String? = null
)

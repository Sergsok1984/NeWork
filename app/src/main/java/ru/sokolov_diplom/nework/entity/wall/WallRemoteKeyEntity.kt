package ru.sokolov_diplom.nework.entity.wall

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WallRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Int,
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}

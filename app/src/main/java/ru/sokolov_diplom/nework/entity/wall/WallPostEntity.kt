package ru.sokolov_diplom.nework.entity.wall

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.entity.post.AttachmentEntity

@Entity
data class WallPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val authorAvatar: String?,
    val authorId: Int,
    val authorJob: String?,
    val content: String,
    val likedByMe: Boolean,
    val link: String? = null,
    val mentionIds: Set<Int> = emptySet(),
    val mentionedMe: Boolean,
    val likeOwnerIds: Set<Int> = emptySet(),
    val ownedByMe: Boolean,
    val published: String,
    @Embedded
    val attachment: AttachmentEntity?
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        authorId,
        authorJob,
        content,
        likedByMe,
        link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        ownedByMe,
        published,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) = WallPostEntity(
            dto.id,
            dto.author,
            dto.authorAvatar,
            dto.authorId,
            dto.authorJob,
            dto.content,
            dto.likedByMe,
            dto.link,
            dto.mentionIds,
            dto.mentionedMe,
            dto.likeOwnerIds,
            dto.ownedByMe,
            dto.published,
            AttachmentEntity.fromDto(dto.attachment)
        )
    }
}


fun List<WallPostEntity>.toDto(): List<Post> = map { it.toDto() }
fun List<Post>.toWallPostEntity(): List<WallPostEntity> = map { WallPostEntity.fromDto(it) }

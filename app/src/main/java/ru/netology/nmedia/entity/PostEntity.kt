package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    @Embedded
    var attachment: AttachmentEntity?,
    val visible: Boolean = true,
) {
    fun toDto() =
        Post(
            id,
            authorId,
            author,
            authorAvatar,
            content,
            published,
            likedByMe,
            likes,
            attachment?.toDto()
        )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                AttachmentEntity.fromDto(dto.attachment),
            )
    }
}

@Entity
data class AttachmentEntity(
    val url: String,
    val description: String,
    val type: AttachmentType
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?): AttachmentEntity? {
            return if (dto != null) AttachmentEntity(dto.url, dto.description, dto.type) else null
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(visible: Boolean = true) = map { PostEntity.fromDto(it) }
    .map { it.copy(visible = visible) }


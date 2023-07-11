package ru.netology.nmedia.dto

sealed interface FeedItem{
    val id: Long
    val published: String
}

data class Post(
    override val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String,
    val content: String,
    override val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment?,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
    override val published: String = "999999999999",
) : FeedItem

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}

data class Time(
    override val id: Long = 0,
    override val published: String,
) : FeedItem



package ru.netology.nmedia.dto

import java.time.OffsetDateTime

sealed interface FeedItem{
    val id: Long
//    val published: String
}

data class Post(
    override val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: OffsetDateTime,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment?,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
) : FeedItem

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}

data class TimeSeparator(
    val term: Term,
//    override val published: String,
) : FeedItem {
    override val id: Long = term.ordinal.toLong()
    enum class Term {
        TODAY,
        YESTERDAY,
        LONG_AGO
    }
}



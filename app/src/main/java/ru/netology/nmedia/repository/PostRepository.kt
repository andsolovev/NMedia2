package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(postsCallback: PostsCallback<List<Post>>)
    fun likeById(id: Long, likeCallback: PostsCallback<Post>)
    fun unlikeById(id: Long, unlikeCallback: PostsCallback<Post>)
    fun save(post: Post, saveCallback: PostsCallback<Post>)
    fun removeById(id: Long, removeCallback: PostsCallback<Long>)
}

interface PostsCallback<T> {
    fun onSuccess(data: T)
    fun onError(e: java.lang.Exception)
}

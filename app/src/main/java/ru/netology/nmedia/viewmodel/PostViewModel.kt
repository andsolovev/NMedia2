package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.lang.Exception

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _error = SingleLiveEvent<Throwable>()
    private val _errorDelete = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable>
        get() = _error
    val errorDelete: LiveData<Throwable>
        get() = _errorDelete
    val errorLike: LiveData<Throwable>
        get() = _error

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAll(object : PostRepository.PostsCallback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                _error.value = Exception("Error loading posts")
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.PostsCallback<Post> {
                override fun onSuccess(data: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                    _error.value = Exception("An error occurred. Please, try again later!")
                }

            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        repository.likeById(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) data else it
                        }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                _error.value = Exception("Unable to like post. Please, try again later!")
            }
        })
    }

    fun unlikeById(id: Long) {
        repository.unlikeById(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) data else it
                        }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                _error.value = Exception("Unable to like post. Please, try again later!")
            }

        })
    }

    fun removeById(id: Long) {

        repository.removeById(id, object : PostRepository.PostsCallback<Unit> {
            val old = _data.value?.posts.orEmpty()
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _errorDelete.value = Exception("Unable to delete post. Please, try again later!")
                _data.postValue(_data.value?.copy(posts = old))

            }

        })
    }
}

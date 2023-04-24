package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.PostsCallback<List<Post>>) {
        PostApi.service.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if(!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }

        })
    }

    override fun likeById(id: Long, callback: PostRepository.PostsCallback<Post>) {
        PostApi.service.likeById(id).enqueue(object : Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun unlikeById(id: Long, callback: PostRepository.PostsCallback<Post>) {
        PostApi.service.unlikeById(id).enqueue(object : Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }
        })
    }

    override fun save(post: Post, callback: PostRepository.PostsCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object : Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if(!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.PostsCallback<Unit>) {
        PostApi.service.deletePostById(id)
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(Unit)
                }
            })
    }
}

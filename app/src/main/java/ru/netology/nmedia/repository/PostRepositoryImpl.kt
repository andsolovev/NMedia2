package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(postsCallback: PostsCallback<List<Post>>) {
        val request: Request = Request.Builder().url("${BASE_URL}/api/posts").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                postsCallback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) postsCallback.onError(Exception(response.message))
                val body = requireNotNull(response.body?.string()) { "body is null" }
                val posts: List<Post> = gson.fromJson(body, typeToken.type)
                postsCallback.onSuccess(posts)
            }
        })
    }

    override fun likeById(id: Long, likeCallback: PostsCallback<Post>) {
        val request: Request = Request.Builder().post(gson.toJson(id).toRequestBody())
            .url("${BASE_URL}/api/posts/$id/likes").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                likeCallback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) likeCallback.onError(Exception(response.message))
                val body = requireNotNull(response.body?.string()) { "body is null" }
                val post = gson.fromJson(body, Post::class.java)
                likeCallback.onSuccess(post)
            }

        })
    }

    override fun unlikeById(id: Long, unlikeCallback: PostsCallback<Post>) {
        val request: Request = Request.Builder().delete(gson.toJson(id).toRequestBody())
            .url("${BASE_URL}/api/posts/$id/likes").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                unlikeCallback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) unlikeCallback.onError(Exception(response.message))
                val body = requireNotNull(response.body?.string()) { "body is null" }
                val post = gson.fromJson(body, Post::class.java)
                unlikeCallback.onSuccess(post)
            }

        })
    }

    override fun save(post: Post, saveCallback: PostsCallback<Post>) {
        val request: Request = Request.Builder().post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                saveCallback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) saveCallback.onError(Exception(response.message))
                saveCallback.onSuccess(post)
            }
        })
    }

    override fun removeById(id: Long, removeCallback: PostsCallback<Long>) {
        val request: Request = Request.Builder().delete().url("${BASE_URL}/api/posts/$id").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                removeCallback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) removeCallback.onError(Exception(response.message))
                removeCallback.onSuccess(id)
            }
        })
    }
}

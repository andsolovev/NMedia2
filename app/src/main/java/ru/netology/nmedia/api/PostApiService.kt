package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PostApiService {
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @DELETE("posts/{id}")
    fun deletePostById(@Path("id") id: Long): Call<Unit>

    @POST("posts/{postId}/likes")
    fun likeById(@Path("postId") id: Long): Call<Post>

    @DELETE("posts/{postId}/likes")
    fun unlikeById(@Path("postId") id: Long): Call<Post>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>
}

object PostApi{
    val service: PostApiService by lazy {
        retrofit.create()
    }
}
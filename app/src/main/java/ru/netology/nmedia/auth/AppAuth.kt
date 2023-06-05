package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import java.io.IOException

class AppAuth private constructor(context: Context) {
    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"

        private var INSTANCE: AppAuth? = null

        fun getInstance(): AppAuth = requireNotNull(INSTANCE) {
            "init() must be called before getInstance()"
        }

        fun init(context: Context) {
            INSTANCE = AppAuth(context)
        }
    }

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data: MutableStateFlow<Token?>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        _data = if (token == null || !prefs.contains(ID_KEY)) {
            prefs.edit { clear() }
            MutableStateFlow(null)
        } else {
            MutableStateFlow(Token(id, token))
        }
    }

    val data = _data.asStateFlow()

    @Synchronized
    fun setToken(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _data.value = Token(id, token)
    }

    @Synchronized
    fun clearAuth() {
        prefs.edit { clear() }
        _data.value = null
    }

    suspend fun update(login: String, password: String) {
        try {
            val response = PostApi.service.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            setToken(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            println(e)
            throw UnknownError
        }
    }

    suspend fun register(login: String, password: String, name: String) {
        try {
            val response = PostApi.service.registerUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            setToken(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registerWithPhoto(login: String, password: String, name: String, file: File) {
        try {
            val avatar = MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody()
            )
            val response = PostApi.service.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                avatar
            )

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            setToken(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}

data class Token(
    val id: Long,
    val token: String,
)
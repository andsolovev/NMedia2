package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.Token
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File


class AuthViewModel : ViewModel() {
    val data: LiveData<Token?> = AppAuth.getInstance().data
        .asLiveData()
    val authorized: Boolean
        get() = data.value != null


    private val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable>
        get() = _error

    private val noAvatar = PhotoModel(null, null)

    private val _avatar = MutableLiveData(noAvatar)
    val avatar: LiveData<PhotoModel>
        get() = _avatar


    fun updateUser(login: String, password: String) =
        viewModelScope.launch {
            try {
                AppAuth.getInstance().update(login, password)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun registerUser(login: String, password: String, name: String) =
        viewModelScope.launch {
            try {
                AppAuth.getInstance().register(login, password, name)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun registerWithPhoto(login: String, password: String, name: String, file: File) =
        viewModelScope.launch {
            try {
                AppAuth.getInstance().registerWithPhoto(login, password, name, file)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun setAvatar(photoModel: PhotoModel) {
        _avatar.value = photoModel
    }

    fun clearAvatar() {
        _avatar.value = null
    }
}

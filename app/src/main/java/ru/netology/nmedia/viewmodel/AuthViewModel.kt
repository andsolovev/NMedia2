package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.Token
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
) : ViewModel() {

    val data: LiveData<Token?> = appAuth.data
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
                appAuth.update(login, password)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun registerUser(login: String, password: String, name: String) =
        viewModelScope.launch {
            try {
                appAuth.register(login, password, name)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun registerWithPhoto(login: String, password: String, name: String, file: File) =
        viewModelScope.launch {
            try {
                appAuth.registerWithPhoto(login, password, name, file)
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

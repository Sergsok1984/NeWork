package ru.sokolov_diplom.nework.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ru.sokolov_diplom.nework.auth.AppAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.dto.PhotoModel
import ru.sokolov_diplom.nework.error.AppError
import ru.sokolov_diplom.nework.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val state = appAuth.state.asLiveData(Dispatchers.Default)
    val authorized: Boolean
        get() = state.value?.id != 0

    private val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable>
        get() = _error

    private val noPhoto = PhotoModel(null, null)

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun updateUser(login: String, password: String) =
        viewModelScope.launch {
            try {
                appAuth.update(login, password)
            } catch (e: Exception) {
                _error.value = e
            }
        }

    fun registerUser(login: String, password: String, name: String, file: File) =
        viewModelScope.launch {
            try {
                appAuth.registerUser(login, password, name, file)
            } catch (e: Exception) {
                println(e)
                throw AppError.from(e)
            }
        }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }
}

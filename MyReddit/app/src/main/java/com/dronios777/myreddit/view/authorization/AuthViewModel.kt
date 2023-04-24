package com.dronios777.myreddit.view.authorization

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronios777.myreddit.AUTH_STRING
import com.dronios777.myreddit.EMPTY
import com.dronios777.myreddit.data.api.ServicesApi.RetrofitServices.apiToken
import com.dronios777.myreddit.data.repository.Repository
import com.dronios777.myreddit.state.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    //токен из сети
    private val _token = MutableSharedFlow<String>()
    val token = _token.asSharedFlow()

    private var accessToken = EMPTY

    //отслеживаем состояние
    private val _state = MutableStateFlow(State.START)
    val loadState = _state.asStateFlow()


    fun getTokenFromSharedPreference(): String? {
        return repository.getTokenStorage()
    }

    fun saveTokenToSharedPreference(tokenKey: String) {
        repository.saveTokenStorage(tokenKey)
    }

    fun createToken(code: String) {
        if (code != EMPTY)
            viewModelScope.launch(Dispatchers.IO) {
                _state.emit(State.LOADING)
                accessToken = try {
                    apiToken.getToken(
                        authHeader = "Basic $encodedAuthString",
                        code = code
                    ).access_token.toString()
                } catch (t: Exception) {
                    _state.emit(State.ERROR.apply { message = t.message.toString() })
                    Log.d("MyLog", "error: ${t.message}").toString()
                }
                _token.emit(accessToken)
                _state.emit(State.SUCCESS)
            }
        Log.d("MyLog", "code for token: $code")

    }

    companion object {
        val encodedAuthString: String =
            Base64.encodeToString(AUTH_STRING.toByteArray(), Base64.NO_WRAP)
    }
}
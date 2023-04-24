package com.dronios777.myreddit.view.user_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronios777.myreddit.data.model.user_info.UserInfo
import com.dronios777.myreddit.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // хранится информация о выбранном пользователе
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    // хранится состояние друг/не друг
    private val _isFriend = MutableStateFlow(false)
    val isFriend = _isFriend.asStateFlow()

    fun getUserInfo(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _userInfo.value = repository.userInfo(user).data
        }
    }

    fun addToFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            userInfo.value?.name?.let {
                repository.addToFriends(it)
                parsingFriends()
            }
        }
    }

    fun parsingFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var isFriend1 = false
                val parsing = repository.myFriends().data?.friends
                if (parsing != null) {
                    for (i in parsing) {
                        if (userInfo.value?.name == i.name) { //уже в друзьях у пользователя
                            isFriend1 = true
                        }
                    }
                }
                _isFriend.value = isFriend1
            } catch (_: Exception) {
            }
        }
    }

    fun removeFriend() {
        viewModelScope.launch(Dispatchers.IO) {
            userInfo.value?.name?.let {
                repository.removeFriend(it)
                parsingFriends()
            }
        }
    }
}
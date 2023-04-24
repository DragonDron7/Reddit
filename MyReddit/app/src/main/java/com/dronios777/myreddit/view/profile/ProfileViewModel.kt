package com.dronios777.myreddit.view.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronios777.myreddit.data.model.friends.Friend
import com.dronios777.myreddit.data.model.me.MeResponse
import com.dronios777.myreddit.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _userInfo = MutableStateFlow<MeResponse?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _friends = MutableStateFlow<List<Friend>?>(null)
    val friends = _friends.asStateFlow()

    val showAlertDialog = MutableLiveData(false)

    fun showAlertDialog(show: Boolean) {
        showAlertDialog.value = show
    }

      fun updateUserInfo() {
         viewModelScope.launch(Dispatchers.IO) {
            _userInfo.value = repository.myInfo()
            _friends.value = repository.myFriends().data?.friends
        }

    }
}
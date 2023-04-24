package com.dronios777.myreddit.view.posts.comments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronios777.myreddit.data.model.posts.CommentsResponse.Data.Comments
import com.dronios777.myreddit.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    // хранится состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //список комментариев для конкретного поста
    private val _comments = MutableStateFlow<List<Comments>?>(emptyList())
    val comments = _comments.asStateFlow()

    //ID поста для поиска комментариев к нему
    private val _postID = MutableStateFlow<String?>(null)
    val postID = _postID.asStateFlow()


      fun comments(postID: String) {
          viewModelScope.launch(Dispatchers.IO) {
              _isLoading.value = true //идёт загрузка
              _postID.value = postID
              // с [1] индекса, т.к. вначале идёт id поста
              _comments.value = repository.getCommentsPost(postID)[1].data?.comments

              val a = _comments.value?.toMutableList()
              /*  if (a != null) {
                    if (a.isNotEmpty()) {
                        a.removeAt(a.lastIndex)
                    }
                    _comments.value = a*/
              _isLoading.value = false//окончена загрузка
          }
      }


    fun vote(item: Comments, dir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (item.commentsData?.name != null) {
                    repository.vote(dir = dir, id = item.commentsData.name.toString().trim())
                    //обновляем список постов
                    _comments.value =
                        postID.value?.let { repository.getCommentsPost(it) }?.get(1)?.data?.comments
                }
            } catch (_: Exception) {
            }
        }
    }




}
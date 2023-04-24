package com.dronios777.myreddit.view.favourites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dronios777.myreddit.BTN_SUB
import com.dronios777.myreddit.PAGE_SIZE
import com.dronios777.myreddit.data.model.posts.CommentsResponse
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse.Data.Posts
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits.Data.Children
import com.dronios777.myreddit.data.repository.Repository
import com.dronios777.myreddit.data.repository.paging_sources.SubscribePostPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    //загруженный список сабреддитов
    private val _subreddits = MutableStateFlow<List<Children>>(emptyList())
    val subreddits = _subreddits.asStateFlow()

    // хранится состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //загруженный список постов
    private val _subredditPosts = MutableStateFlow<Flow<PagingData<Posts>>?>(null)
    val subredditPosts = _subredditPosts.asStateFlow()

    //какая кнопка нажата (btnSub/btnPosts)
    private val _btnClick = MutableLiveData(BTN_SUB)
    val btnClick = _btnClick

    //список комментариев для конкретного поста
    private val _comments =
        MutableStateFlow<List<CommentsResponse.Data.Comments>?>(emptyList())
    val comments = _comments.asStateFlow()

    fun btnClick(btn: String) {
        _btnClick.value = btn
    }


    fun getFavouritesSubreddits() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка
                //загружаю список сабреддитов, на которые подписан user
                val parsing = repository.getUserSubscriptions().body()?.data?.children
                if (parsing != null) {
                    _subreddits.value = parsing
                }
                _isLoading.value = false //окончена загрузка
            } catch (_: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun joinOrLeave(item: Children, action: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                item.data?.let {
                    it.displayNamePrefixed?.let { it1 ->
                        repository.joinOrLeaveSub(
                            action = action,
                            subName = it1
                        )
                    }
                }
                refresh()
            } catch (_: Exception) {
            }
        }
    }


    fun refresh() {
        getFavouritesSubreddits()
    }

    fun subscribePost() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка

                val favouritesPostPagingSource = SubscribePostPagingSource(repository)
                _subredditPosts.value = Pager(
                    config = PagingConfig(
                        pageSize = PAGE_SIZE
                    ),
                    pagingSourceFactory = { favouritesPostPagingSource }
                ).flow.cachedIn(viewModelScope)

                _isLoading.value = false //окончена загрузка
            } catch (_: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun vote(item: Posts, dir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (item.postData?.name != null) {
                    repository.vote(dir = dir, id = item.postData.name.toString().trim())

                    //обновляем список постов
                    val favouritesPostPagingSource = SubscribePostPagingSource(repository)
                    _subredditPosts.value = Pager(
                        config = PagingConfig(
                            pageSize = PAGE_SIZE
                        ),
                        pagingSourceFactory = { favouritesPostPagingSource }
                    ).flow.cachedIn(viewModelScope)
                }
            } catch (_: Exception) {
            }
        }
    }

}
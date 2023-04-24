package com.dronios777.myreddit.view.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dronios777.myreddit.PAGE_SIZE
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse.Data.Posts
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits.Data.Children
import com.dronios777.myreddit.data.repository.Repository
import com.dronios777.myreddit.data.repository.paging_sources.PostPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditPostsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    // хранится состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //данные о сабреддите
    private val _subreddit = MutableStateFlow<Children?>(null)
    val subreddit = _subreddit.asStateFlow()

    //загруженный список постов
    private val _subredditPosts = MutableStateFlow<Flow<PagingData<Posts>>?>(null)
    val subredditPosts = _subredditPosts.asStateFlow()

    fun getSubredditPosts(item: Children) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка

                _subreddit.value = item

                val postPagingSource1 = PostPagingSource(repository)
                item.data?.displayName?.let {
                    postPagingSource1.subInit(it)
                    _subredditPosts.value = Pager(
                        config = PagingConfig(
                            pageSize = PAGE_SIZE,
                        ),
                        pagingSourceFactory = { postPagingSource1 }

                    ).flow.cachedIn(viewModelScope)
                }
                _isLoading.value = false

            } catch (_: Exception) {
                _isLoading.value = false //окончена загрузка
            }
        }
    }

    fun joinOrLeave(action: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _subreddit.value?.data?.let {
                    it.displayNamePrefixed?.let { it1 ->
                        repository.joinOrLeaveSub(
                            action = action,
                            subName = it1
                        )
                    }
                }
                _subreddit.value?.let { getSubredditPosts(it) }

            } catch (_: Exception) {
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка}
                repository.searchSubreddits(query).data?.children?.let { parsing(it) }
                _isLoading.value = false //окончена загрузка
            } catch (_: Exception) {
            }
        }
    }

    private fun parsing(subreddits: List<Children>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //загружаю список подписок пользователя на сабреддиты
                val parsing: ResponseSubreddits? = repository.getUserSubscriptions().body()
                if (parsing != null) {
                    for (i0 in subreddits)
                        for (i in parsing.data?.children!!) {
                            //сравниваю открытый сабреддит со списком сообществ, на которые подписан user
                            if ((subreddits.first().data?.displayNamePrefixed) == (i.data?.displayNamePrefixed)
                            ) {
                                subreddits.first().data?.userIsSubscriber = true
                            }
                        }

                    _subreddit.value = subreddits[0]
                }
            } catch (_: Exception) {
            }
        }
    }


    fun vote(item: Posts, dir: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (item.postData?.name != null) {
                    repository.vote(dir = dir, id = item.postData.name.toString().trim())

                    val postPagingSource1 = PostPagingSource(repository)
                    _subredditPosts.value =
                        _subreddit.value?.data?.let {
                            it.displayName?.let { it1 -> postPagingSource1.subInit(it1) }
                            Pager(
                                config = PagingConfig(
                                    pageSize = PAGE_SIZE
                                ),
                                pagingSourceFactory = { postPagingSource1 }
                            ).flow.cachedIn(viewModelScope)
                        }
                }
            } catch (_: Exception) {
            }
        }
    }


}
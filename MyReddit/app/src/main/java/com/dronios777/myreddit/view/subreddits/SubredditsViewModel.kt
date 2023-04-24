package com.dronios777.myreddit.view.subreddits

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronios777.myreddit.POPULAR
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits.Data.Children
import com.dronios777.myreddit.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    //загруженный список сабреддитов
    private val _subreddits = MutableStateFlow<List<Children>>(emptyList())
    val subreddits = _subreddits.asStateFlow()

    // хранится состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    //состояние типа поиска сабреддитов (new/popular)
    private val _subWhere = MutableLiveData(POPULAR)
    val subWhere = _subWhere


    fun subredditPath(where: String) {
        _subWhere.value = where
    }

    private fun getSubredditsInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка
                repository.getSubreddits(_subWhere.value.toString()).data?.children?.let {
                    parsing(
                        it
                    )
                }
                _isLoading.value = false //окончена загрузка
                _isError.value = false//нет ошибок
            } catch (_: Exception) {
                _isLoading.value = false
                _isError.value = true //ошибка загрузки
            }
        }
    }

    fun refresh() {
        getSubredditsInfo()
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true //идёт загрузка
                repository.searchSubreddits(query).data?.children?.let { parsing(it) }
                _isLoading.value = false //окончена загрузка
            } catch (_: Exception) {  //скорей всего нет сети
                _isLoading.value = false
                _isError.value = true //ошибка загрузки
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
                _isError.value = true //ошибка
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
                            //сравниваю список с сабреддитами, на которые подписан user и с теми что пришли (по id)
                            if ((subreddits.first().data?.id
                                    ?: subreddits.first().data?.displayNamePrefixed) ==
                                (i.data?.id ?: i.data?.displayNamePrefixed)
                            ) {
                                subreddits.first().data?.userIsSubscriber = true
                            }
                        }
                    _subreddits.value = subreddits
                }
            } catch (_: Exception) {
            }
        }
    }
}
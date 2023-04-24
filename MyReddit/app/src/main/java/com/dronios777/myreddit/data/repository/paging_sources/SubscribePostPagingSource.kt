package com.dronios777.myreddit.data.repository.paging_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dronios777.myreddit.FIRST_PAGE
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse.Data.Posts
import com.dronios777.myreddit.data.repository.Repository
import javax.inject.Inject

class SubscribePostPagingSource @Inject constructor(private val repository: Repository) :
    PagingSource<String, Posts>() {

    private var key: String? =
        null //для проверки на конец списка страниц (сервер шлёт данные бессконечно, рекурсией)

    override fun getRefreshKey(state: PagingState<String, Posts>): String = FIRST_PAGE

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Posts> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            repository.subscribePost(page)
        }.fold(
            onSuccess = {

                if (key == it.data?.after.toString().trim() /*&& it.data?.posts?.size!! >= 0*/) {
                    LoadResult.Page(
                        data = listOf(),
                        prevKey = null,
                        nextKey = it.data?.after ?: FIRST_PAGE
                    )
                } else {
                    key = it.data?.after.toString().trim()
                    it.data?.posts?.let { posts ->
                        LoadResult.Page(
                            data = posts,
                            prevKey = null,
                            nextKey = it.data.after ?: FIRST_PAGE
                        )
                    }
                }
            },
            onFailure = { LoadResult.Error(it) }
        )!!
    }
}